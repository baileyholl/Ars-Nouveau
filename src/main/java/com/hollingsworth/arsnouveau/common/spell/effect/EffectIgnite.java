package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.GlyphLib;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public class EffectIgnite  extends AbstractEffect {
    public static EffectIgnite INSTANCE = new EffectIgnite();

    private EffectIgnite() {
        super(GlyphLib.EffectIgniteID, "Ignite");
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {

        int duration = (int) (POTION_TIME.get() + EXTEND_TIME.get() * spellStats.getDurationMultiplier());
        rayTraceResult.getEntity().setSecondsOnFire(duration);
    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        if(spellStats.hasBuff(AugmentSensitive.INSTANCE))
            return;
        if(world.getBlockState((rayTraceResult).getBlockPos().above()).getMaterial().isReplaceable()) {
            Direction face = (rayTraceResult).getDirection();
            for (BlockPos pos : SpellUtil.calcAOEBlocks(shooter, (rayTraceResult).getBlockPos(), rayTraceResult, spellStats)) {
                BlockPos blockpos1 = pos.relative(face);
                if (BaseFireBlock.canBePlacedAt(world, blockpos1, face) && BlockUtil.destroyRespectsClaim(getPlayer(shooter, (ServerLevel) world), world, blockpos1)) {
                    BlockState blockstate1 = BaseFireBlock.getState(world, blockpos1);
                    world.setBlock(blockpos1, blockstate1, 11);
                }
            }
        }
    }

    @Override
    public void buildConfig(ForgeConfigSpec.Builder builder) {
        super.buildConfig(builder);
        addExtendTimeConfig(builder, 2);
        addPotionConfig(builder, 3);
    }

    @Override
    public boolean wouldSucceed(HitResult rayTraceResult, Level world, LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        return livingEntityHitSuccess(rayTraceResult) || rayTraceResult instanceof BlockHitResult && world.getBlockState(((BlockHitResult) rayTraceResult).getBlockPos().above()).getMaterial() == Material.AIR;
    }

    @Override
    public int getDefaultManaCost() {
        return 15;
    }

    @Override
    public Tier getTier() {
        return Tier.ONE;
    }

    @Nullable
    @Override
    public Item getCraftingReagent() {
        return Items.FLINT_AND_STEEL;
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentExtendTime.INSTANCE , AugmentAOE.INSTANCE, AugmentPierce.INSTANCE, AugmentDurationDown.INSTANCE, AugmentSensitive.INSTANCE);
    }

    @Override
    public String getBookDescription() {
        return "Sets blocks and mobs on fire for a short time. Sensitive will stop this spell from igniting blocks.";
    }

    @Nonnull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.ELEMENTAL_FIRE);
    }
}
