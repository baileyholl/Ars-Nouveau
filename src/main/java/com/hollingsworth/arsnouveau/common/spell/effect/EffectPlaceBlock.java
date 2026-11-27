package com.hollingsworth.arsnouveau.common.spell.effect;

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
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.BlockSnapshot;
import net.neoforged.neoforge.event.level.BlockEvent;
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
        Direction direction = Direction.SOUTH;
        var sensitiveCount = spellStats.getBuffCount(AugmentSensitive.INSTANCE);
        if (sensitiveCount > 0) {
            direction = Direction.getNearest(spellContext.getCaster().getFacingVec());
            if (sensitiveCount > 1) {
                direction = direction.getOpposite();
            }
        }
        for (BlockPos pos1 : posList) {
            pos1 = getIntendedTargetPos(rayTraceResult, pos1);
            if (!world.isInWorldBounds(pos1))
                continue;
            boolean notReplaceable = !world.getBlockState(pos1).canBeReplaced();
            if (notReplaceable)
                continue;
            var event = NeoForge.EVENT_BUS.post(new BlockEvent.EntityPlaceEvent(BlockSnapshot.create(world.dimension(), world, pos1), world.getBlockState(pos1), fakePlayer));
            if (event.isCanceled())
                continue;
            place(new BlockHitResult(new Vec3(pos1.getX(), pos1.getY(), pos1.getZ()), rayTraceResult.getDirection(), pos1, false), world, shooter, spellStats, spellContext, resolver, direction);
        }
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        var entity = rayTraceResult.getEntity();
        onResolveBlock(new BlockHitResult(entity.position, Direction.DOWN, entity.blockPosition().below(), true), world, shooter, spellStats, spellContext, resolver);
    }

    public void place(BlockHitResult resolveResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver, Direction direction) {
        InventoryManager manager = spellContext.getCaster().getInvManager();
        ExtractedStack extractItem = spellStats.isRandomized() ? manager.extractRandomItem(i -> !i.isEmpty() && i.getItem() instanceof BlockItem, 1) : manager.extractItem(i -> !i.isEmpty() && i.getItem() instanceof BlockItem, 1);
        if (extractItem.isEmpty())
            return;
        InteractionResult resultType = attemptPlace(world, extractItem.stack, (BlockItem) extractItem.stack.getItem(), resolveResult, direction);
        if (InteractionResult.FAIL != resultType) {
            ShapersFocus.tryPropagateBlockSpell(resolveResult, world, shooter, spellContext, resolver);
        }
        extractItem.returnOrDrop(world, shooter.getOnPos());
    }

    @Deprecated(forRemoval = true)
    public static InteractionResult attemptPlace(Level world, ItemStack stack, BlockItem item, BlockHitResult result, Player fakePlayer) {
        return attemptPlace(world, stack, item, result, fakePlayer.getNearestViewDirection());
    }

    public static InteractionResult attemptPlace(Level world, ItemStack stack, BlockItem item, BlockHitResult result, Direction direction) {
        var context = new DispenserBehaviorRegistry.UnbiasedDirectionalPlaceContext(world, result.getBlockPos(), direction, stack, direction);
        return item.place(context);
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
