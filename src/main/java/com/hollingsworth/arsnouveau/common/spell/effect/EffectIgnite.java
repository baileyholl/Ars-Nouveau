package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCandleBlock;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;

public class EffectIgnite extends AbstractEffect {
    public static EffectIgnite INSTANCE = new EffectIgnite();

    private EffectIgnite() {
        super(GlyphLib.EffectIgniteID, "Ignite");
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        int duration = (int) (POTION_TIME.get() + EXTEND_TIME.get() * spellStats.getDurationMultiplier());
        rayTraceResult.getEntity().setRemainingFireTicks(duration * 20);
    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        if (spellStats.isSensitive()) {
            BlockPos target = rayTraceResult.getBlockPos().relative(rayTraceResult.getDirection());
            if(world.getBlockState(target).canBeReplaced()) {
                world.setBlock(target, BlockRegistry.MAGIC_FIRE.get().getStateForPlacement(world, target), 3);
            }
            return;
        }
        BlockState hitState = world.getBlockState(rayTraceResult.getBlockPos());
        if (hitState.getBlock() instanceof CandleBlock && CandleBlock.canLight(hitState) || hitState.getBlock() instanceof CampfireBlock && CampfireBlock.canLight(hitState)) {
            AbstractCandleBlock.setLit(world, hitState, rayTraceResult.getBlockPos(), true);
            return;
        }

        if (world.getBlockState((rayTraceResult).getBlockPos().above()).canBeReplaced()) {
            Direction face = (rayTraceResult).getDirection();
            for (BlockPos pos : SpellUtil.calcAOEBlocks(shooter, (rayTraceResult).getBlockPos(), rayTraceResult, spellStats)) {
                BlockPos blockpos1 = pos.relative(face);
                if (BaseFireBlock.canBePlacedAt(world, blockpos1, face) && BlockUtil.destroyRespectsClaim(getPlayer(shooter, (ServerLevel) world), world, blockpos1)) {
                    BlockState blockstate1 = BaseFireBlock.getState(world, blockpos1);
                    world.setBlock(blockpos1, blockstate1, 3);
                    world.updateNeighborsAt(blockpos1, blockstate1.getBlock());
                }
            }
        }
    }

    @Override
    public void buildConfig(ModConfigSpec.Builder builder) {
        super.buildConfig(builder);
        addExtendTimeConfig(builder, 2);
        addPotionConfig(builder, 3);
    }

    @Override
    protected void addDefaultAugmentLimits(Map<ResourceLocation, Integer> defaults) {
        super.addDefaultAugmentLimits(defaults);
        defaults.put(AugmentSensitive.INSTANCE.getRegistryName(), 1);
    }

    @Override
    public void addAugmentDescriptions(Map<AbstractAugment, String> map) {
        super.addAugmentDescriptions(map);
        map.put(AugmentExtendTime.INSTANCE, "Increases the fire duration.");
        map.put(AugmentDurationDown.INSTANCE, "Decreases the fire duration.");
        map.put(AugmentSensitive.INSTANCE, "Creates a short-lived magic fire that will not spread or destroy blocks instead of normal fire.");
    }

    @Override
    public int getDefaultManaCost() {
        return 15;
    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.ONE;
    }

   @NotNull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentExtendTime.INSTANCE, AugmentAOE.INSTANCE, AugmentPierce.INSTANCE, AugmentDurationDown.INSTANCE, AugmentSensitive.INSTANCE);
    }

    @Override
    public String getBookDescription() {
        return "Sets blocks and mobs on fire for a short time. Sensitive will summon Mage Fire, a shorter lived fire that will not spread or destroy blocks.";
    }

   @NotNull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.ELEMENTAL_FIRE);
    }
}
