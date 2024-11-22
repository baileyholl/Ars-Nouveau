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
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.BlockSnapshot;
import net.neoforged.neoforge.event.level.BlockEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
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
        for (BlockPos pos1 : posList) {
            if (!world.isInWorldBounds(pos1))
                continue;
            pos1 = rayTraceResult.isInside() ? pos1 : pos1.relative(rayTraceResult.getDirection());
            boolean notReplaceable = !world.getBlockState(pos1).canBeReplaced();
            if (notReplaceable)
                continue;
            var event = NeoForge.EVENT_BUS.post(new BlockEvent.EntityPlaceEvent(BlockSnapshot.create(world.dimension(), world, pos1), world.getBlockState(pos1), fakePlayer));
            if (event.isCanceled())
                continue;
            place(new BlockHitResult(new Vec3(pos1.getX(), pos1.getY(), pos1.getZ()), rayTraceResult.getDirection(), pos1, false), world, shooter, spellStats, spellContext, resolver, fakePlayer);
        }
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
        fakePlayer.setItemInHand(InteractionHand.MAIN_HAND, stack);
        BlockPlaceContext context = new BlockPlaceContext(fakePlayer, InteractionHand.MAIN_HAND, stack, result);
        return item.place(context);
    }

    @Override
    public int getDefaultManaCost() {
        return 10;
    }

    @NotNull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentAOE.INSTANCE, AugmentPierce.INSTANCE, AugmentRandomize.INSTANCE);
    }

    @Override
    public String getBookDescription() {
        return "Places blocks from the casters inventory. If a player is casting this, this spell will place blocks from the hot bar first.";
    }

    @NotNull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.MANIPULATION);
    }
}
