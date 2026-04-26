package com.hollingsworth.arsnouveau.common.spell.effect;

import com.google.common.collect.Lists;
import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.item.inv.ExtractedStack;
import com.hollingsworth.arsnouveau.api.item.inv.InventoryManager;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.items.curios.ShapersFocus;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentPierce;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentRandomize;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSensitive;
import com.hollingsworth.arsnouveau.setup.registry.DispenserBehaviorRegistry;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.BlockSnapshot;
import net.neoforged.neoforge.event.EventHooks;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class EffectPlaceBlock extends AbstractEffect {
    public static EffectPlaceBlock INSTANCE = new EffectPlaceBlock();

    private EffectPlaceBlock() {
        super(GlyphLib.EffectPlaceBlockID, "Place Block");
    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        List<BlockPos> posList = SpellUtil.calcAOEBlocks(shooter, rayTraceResult.getBlockPos(), rayTraceResult, spellStats);
        Player fakePlayer = ANFakePlayer.getPlayer((ServerLevel) world, shooter instanceof Player player ? player.getUUID() : null);
        var sensitiveCount = spellStats.getBuffCount(AugmentSensitive.INSTANCE);
        if (sensitiveCount > 0) {
            fakePlayer.setPos(spellContext.getUnwrappedCaster().position);
            var lookLoc = rayTraceResult.getLocation();
            if (sensitiveCount > 1) {
                fakePlayer.setPos(fakePlayer.position.subtract(fakePlayer.position.subtract(lookLoc).scale(2)));
            }
            fakePlayer.lookAt(EntityAnchorArgument.Anchor.EYES, lookLoc);
        }
        for (BlockPos pos1 : posList) {
            if (!world.isInWorldBounds(pos1))
                continue;
            pos1 = rayTraceResult.isInside() ? pos1 : pos1.relative(rayTraceResult.getDirection());
            boolean notReplaceable = !world.getBlockState(pos1).canBeReplaced();
            if (notReplaceable)
                continue;
            place(new BlockHitResult(new Vec3(pos1.getX(), pos1.getY(), pos1.getZ()), rayTraceResult.getDirection(), pos1, false), world, shooter, spellStats, spellContext, resolver, fakePlayer);
        }
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        var entity = rayTraceResult.getEntity();
        onResolveBlock(new BlockHitResult(entity.position, Direction.DOWN, entity.blockPosition().below(), true), world, shooter, spellStats, spellContext, resolver);
    }

    public void place(BlockHitResult resolveResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver, Player fakePlayer) {
        InventoryManager manager = spellContext.getCaster().getInvManager();
        ExtractedStack extractItem = spellStats.isRandomized() ? manager.extractRandomItem(i -> !i.isEmpty() && i.getItem() instanceof BlockItem, 1) : manager.extractItem(i -> !i.isEmpty() && i.getItem() instanceof BlockItem, 1);
        if (extractItem.isEmpty())
            return;
        InteractionResult resultType = attemptPlace(world, extractItem.stack, (BlockItem) extractItem.stack.getItem(), resolveResult, fakePlayer);
        if (InteractionResult.FAIL != resultType) {
            ShapersFocus.tryPropagateBlockSpell(resolveResult, world, shooter, spellContext, resolver);
        }
        extractItem.returnOrDrop(world, shooter.getOnPos());
    }

    public static InteractionResult attemptPlace(Level world, ItemStack stack, BlockItem item, BlockHitResult result, Player fakePlayer) {
        int size = stack.getCount();
        DataComponentMap components = stack.getComponents();
        fakePlayer.setItemInHand(InteractionHand.MAIN_HAND, stack);
        Direction nearestDirection = fakePlayer.getNearestViewDirection();
        var copy = stack.copy();
        var context = new DispenserBehaviorRegistry.UnbiasedDirectionalPlaceContext(world, result.getBlockPos(), nearestDirection, stack, nearestDirection);
        world.captureBlockSnapshots = true;
        var ret = item.place(context);
        if (stack.isEmpty()) {
            EventHooks.onPlayerDestroyItem(fakePlayer, copy, context.getHand());
        }
        world.captureBlockSnapshots = false;

        if (ret.consumesAction()) {
            int newSize = stack.getCount();
            DataComponentMap newComponents = stack.getComponents();

            @SuppressWarnings("unchecked")
            List<BlockSnapshot> blockSnapshots = (List<BlockSnapshot>) world.capturedBlockSnapshots.clone();
            world.capturedBlockSnapshots.clear();

            stack.setCount(size);
            stack.applyComponents(components);

            boolean placeFailure = false;
            if (blockSnapshots.size() > 1) {
                placeFailure = EventHooks.onMultiBlockPlace(fakePlayer, blockSnapshots, result.getDirection());
            } else if (blockSnapshots.size() == 1) {
                placeFailure = EventHooks.onBlockPlace(fakePlayer, blockSnapshots.getFirst(), result.getDirection());
            }

            if (placeFailure) {
                ret = InteractionResult.FAIL;
                for (BlockSnapshot blocksnapshot : Lists.reverse(blockSnapshots)) {
                    world.restoringBlockSnapshots = true;
                    blocksnapshot.restore(blocksnapshot.getFlags() | Block.UPDATE_CLIENTS);
                    world.restoringBlockSnapshots = false;
                }
            } else {
                stack.setCount(newSize);
                stack.applyComponents(newComponents);

                for (BlockSnapshot snap : blockSnapshots) {
                    int updateFlag = snap.getFlags();
                    BlockState oldBlock = snap.getState();
                    BlockState newBlock = world.getBlockState(snap.getPos());
                    newBlock.onPlace(world, snap.getPos(), oldBlock, false);

                    world.markAndNotifyBlock(snap.getPos(), world.getChunkAt(snap.getPos()), oldBlock, newBlock, updateFlag, 512);
                }
            }
        }

        return ret;
    }

    @Override
    public int getDefaultManaCost() {
        return 10;
    }

    @NotNull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentAOE.INSTANCE, AugmentPierce.INSTANCE, AugmentRandomize.INSTANCE, AugmentSensitive.INSTANCE);
    }

    @Override
    protected void addDefaultAugmentLimits(Map<ResourceLocation, Integer> defaults) {
        defaults.put(AugmentSensitive.INSTANCE.getRegistryName(), 2);
    }

    @Override
    public void addAugmentDescriptions(Map<AbstractAugment, String> map) {
        super.addAugmentDescriptions(map);
        addBlockAoeAugmentDescriptions(map);
        map.put(AugmentSensitive.INSTANCE, "Places the block with the caster's facing direction. Using two makes it place in the opposite direction.");
    }

    @Override
    public String getBookDescription() {
        return "Places blocks from the casters inventory. If cast by a player, this spell will place blocks from the hot bar first. Casting on an entity will place the blocks beneath the entity in the up direction. Sensitive causes the block to be placed in the caster's facing direction, two Sensitives will place it in the opposite direction.";
    }

    @NotNull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.MANIPULATION);
    }
}
