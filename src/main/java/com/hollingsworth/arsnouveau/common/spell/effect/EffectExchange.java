package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.item.inv.ExtractedStack;
import com.hollingsworth.arsnouveau.api.item.inv.InventoryManager;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.LootUtil;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.block.IntangibleAirBlock;
import com.hollingsworth.arsnouveau.common.items.curios.ShapersFocus;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import com.hollingsworth.arsnouveau.common.util.HolderHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

public class EffectExchange extends AbstractEffect {
    public static EffectExchange INSTANCE = new EffectExchange();

    private EffectExchange() {
        super(GlyphLib.EffectExchangeID, "Exchange");
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        Entity entity = rayTraceResult.getEntity();
        Vec3 origLoc = shooter.position;
        if (!EventHooks.onEnderTeleport(shooter, entity.getX(), entity.getY(), entity.getZ()).isCanceled())
            shooter.teleportTo(entity.getX(), entity.getY(), entity.getZ());
        if (!(entity instanceof LivingEntity living) || !EventHooks.onEnderTeleport(living, origLoc.x(), origLoc.y(), origLoc.z()).isCanceled()) {
            entity.teleportTo(origLoc.x(), origLoc.y(), origLoc.z());
        }
    }

    @Override
    public void onResolveBlock(BlockHitResult result, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        List<BlockPos> posList = SpellUtil.calcAOEBlocks(shooter, result.getBlockPos(), result, spellStats.getAoeMultiplier(), spellStats.getBuffCount(AugmentPierce.INSTANCE));
        BlockState origState = world.getBlockState(result.getBlockPos());
        ANFakePlayer fakePlayer = ANFakePlayer.getPlayer((ServerLevel) world);
        Block firstBlock = null;
        InventoryManager manager = spellContext.getCaster().getInvManager();
        for (BlockPos pos1 : posList) {
            BlockState state = world.getBlockState(pos1);

            if (!canBlockBeHarvested(spellStats, world, pos1) || origState.getBlock() != state.getBlock()
                || !world.getBlockState(pos1).isAir() && world.getBlockState(pos1).getBlock() instanceof IntangibleAirBlock
                || !BlockUtil.destroyRespectsClaim(getPlayer(shooter, (ServerLevel) world), world, pos1)) {
                continue;
            }
            Block finalFirstBlock = firstBlock;
            ExtractedStack extractedStack;

            if (spellStats.isRandomized())
                extractedStack = manager.extractRandomItem(i -> i.getItem() instanceof BlockItem, 1);
            else extractedStack = manager.extractItem(i -> {
                if (i.getItem() instanceof BlockItem blockItem) {
                    if (finalFirstBlock == null) {
                        return true;
                    }
                    return blockItem.getBlock() == finalFirstBlock;
                }
                return false;
            }, 1);

            if (extractedStack.isEmpty()) {
                continue;
            }
            if (firstBlock == null && extractedStack.stack.getItem() instanceof BlockItem blockItem) {
                firstBlock = blockItem.getBlock();
            }
            attemptPlace(extractedStack.getStack(), world, pos1, result, shooter, fakePlayer, spellContext, resolver);
            extractedStack.replaceAndReturnOrDrop(extractedStack.getStack(), world, shooter.getOnPos());
        }
    }

    public void attemptPlace(ItemStack stack, Level world, BlockPos pos1, BlockHitResult result, LivingEntity shooter, Player fakePlayer, SpellContext spellContext, SpellResolver resolver) {
        BlockItem item = (BlockItem) stack.getItem();
        ItemStack tool = LootUtil.getDefaultFakeTool();
        tool.enchant(HolderHelper.unwrap(world, Enchantments.SILK_TOUCH), 1);
        fakePlayer.setItemInHand(InteractionHand.MAIN_HAND, stack);
        BlockPlaceContext context = BlockPlaceContext.at(new BlockPlaceContext(new UseOnContext(fakePlayer, InteractionHand.MAIN_HAND, result)), pos1.relative(result.getDirection().getOpposite()), result.getDirection());
        BlockState placeState = item.getBlock().getStateForPlacement(context);
        if (placeState != null && placeState.getBlock() == world.getBlockState(pos1).getBlock())
            return;

        if (!BlockUtil.breakExtraBlock((ServerLevel) world, pos1, tool, shooter.getUUID(), true)) {
            return;
        }
        if (placeState == null)
            return;
        world.setBlock(pos1, placeState, 3);
        item.getBlock().setPlacedBy(world, pos1, placeState, shooter, stack);
        BlockItem.updateCustomBlockEntityTag(world, shooter instanceof Player player ? player : fakePlayer, pos1, stack);
        stack.shrink(1);
        ShapersFocus.tryPropagateBlockSpell(new BlockHitResult(new Vec3(pos1.getX(), pos1.getY(), pos1.getZ()), result.getDirection(), pos1, false), world, shooter, spellContext, resolver);
    }


    @Override
    public SpellTier defaultTier() {
        return SpellTier.TWO;
    }

    @NotNull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(
                AugmentAmplify.INSTANCE, AugmentDampen.INSTANCE,
                AugmentPierce.INSTANCE,
                AugmentAOE.INSTANCE, AugmentRandomize.INSTANCE
        );
    }

    @Override
    public String getBookDescription() {
        return "When used on blocks, exchanges the blocks in the players hotbar for the blocks hit as if they were mined with silk touch. Can be augmented with AOE, and Amplify is required for swapping blocks of higher hardness. "
               + "When used on entities, the locations of the caster and the entity hit are swapped.";
    }

    @Override
    public int getDefaultManaCost() {
        return 50;
    }

    @NotNull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.MANIPULATION);
    }
}
