package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentPierce;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.util.FakePlayerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public class EffectGrow  extends AbstractEffect {
    public static EffectGrow INSTANCE = new EffectGrow();

    private EffectGrow() {
        super(GlyphLib.EffectGrowID, "Grow");
    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        for(BlockPos blockpos : SpellUtil.calcAOEBlocks(shooter,  rayTraceResult.getBlockPos(), rayTraceResult, spellStats)){
            ItemStack stack = new ItemStack(Items.BONE_MEAL, 64);
            if(BlockUtil.destroyRespectsClaim(shooter, world, blockpos) && world instanceof ServerLevel) {
                BoneMealItem.applyBonemeal(stack, world, blockpos, FakePlayerFactory.getMinecraft((ServerLevel) world));
            }
        }
    }

    @Override
    public boolean wouldSucceed(HitResult rayTraceResult, Level world, LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        if(!(rayTraceResult instanceof BlockHitResult))
            return false;
        BlockPos pos = ((BlockHitResult) rayTraceResult).getBlockPos();
        return world.getBlockState(pos).getBlock() instanceof BonemealableBlock
                && ((BonemealableBlock) world.getBlockState(pos).getBlock()).isValidBonemealTarget(world, pos, world.getBlockState(pos), world.isClientSide);
    }

    @Override
    public int getDefaultManaCost() {
        return 70;
    }

    @Nullable
    @Override
    public Item getCraftingReagent() {
        return Items.BONE_BLOCK;
    }

    @Override
    public SpellTier getTier() {
        return SpellTier.TWO;
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentAOE.INSTANCE, AugmentPierce.INSTANCE);
    }

    @Override
    public String getBookDescription() {
        return "Causes plants to accelerate in growth as if they were bonemealed.";
    }

    @Nonnull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.ELEMENTAL_EARTH);
    }
}
