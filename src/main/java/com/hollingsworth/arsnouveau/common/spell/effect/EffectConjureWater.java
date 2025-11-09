package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.items.curios.ShapersFocus;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentExtendTime;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentPierce;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSensitive;
import com.hollingsworth.arsnouveau.setup.registry.ModPotions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class EffectConjureWater extends AbstractEffect implements IPotionEffect {

    public static EffectConjureWater INSTANCE = new EffectConjureWater();

    private EffectConjureWater() {
        super(GlyphLib.EffectConjureWaterID, "Conjure Water");
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        Entity entity = rayTraceResult.getEntity();
        if (entity.isOnFire()) {
            entity.clearFire();
        }
        if (entity instanceof LivingEntity livingEntity && spellStats.getDurationMultiplier() > 0) {
            applyConfigPotion(livingEntity, ModPotions.SOAKED_EFFECT, spellStats);
        }
        if (spellStats.isSensitive() && !world.dimensionType().ultraWarm()) {
            placeWater((ServerLevel) world, shooter, spellContext, resolver, entity.blockPosition(), Direction.UP);
        }

    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        double aoeBuff = spellStats.getAoeMultiplier();
        List<BlockPos> posList = SpellUtil.calcAOEBlocks(shooter, rayTraceResult.getBlockPos(), rayTraceResult, aoeBuff, spellStats.getBuffCount(AugmentPierce.INSTANCE));
        if (world.dimensionType().ultraWarm())
            return;
        for (BlockPos pos1 : posList) {
            placeWater((ServerLevel) world, shooter, spellContext, resolver, pos1, rayTraceResult.getDirection());
        }
    }

    private void placeWater(ServerLevel world, @NotNull LivingEntity shooter, SpellContext spellContext, SpellResolver resolver, BlockPos pos1, Direction direction) {
        if (!BlockUtil.destroyRespectsClaim(getPlayer(shooter, world), world, pos1))
            return;
        if (!world.isInWorldBounds(pos1))
            return;
        BlockState hitState = world.getBlockState(pos1);
        if (hitState.getBlock() instanceof LiquidBlockContainer liquidBlockContainer && liquidBlockContainer.canPlaceLiquid(getPlayer(shooter, world), world, pos1, world.getBlockState(pos1), Fluids.WATER)) {
            liquidBlockContainer.placeLiquid(world, pos1, hitState, Fluids.WATER.getSource(true));
            ShapersFocus.tryPropagateBlockSpell(new BlockHitResult(
                    new Vec3(pos1.getX(), pos1.getY(), pos1.getZ()), direction, pos1, false
            ), world, shooter, spellContext, resolver);
        } else {
            var state = world.getBlockState(pos1);
            if (!state.canBeReplaced(Fluids.WATER)) {
                pos1 = pos1.relative(direction);
                state = world.getBlockState(pos1);
                if (!state.canBeReplaced(Fluids.WATER)) {
                    return;
                }
            }

            world.setBlockAndUpdate(pos1, Blocks.WATER.defaultBlockState());
            ShapersFocus.tryPropagateBlockSpell(new BlockHitResult(
                    new Vec3(pos1.getX(), pos1.getY(), pos1.getZ()), direction, pos1, false
            ), world, shooter, spellContext, resolver);
        }
    }

    @Override
    public int getDefaultManaCost() {
        return 80;
    }

    @NotNull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentAOE.INSTANCE, AugmentPierce.INSTANCE, AugmentExtendTime.INSTANCE, AugmentSensitive.INSTANCE);
    }

    @Override
    public void addAugmentDescriptions(Map<AbstractAugment, String> map) {
        super.addAugmentDescriptions(map);
        addBlockAoeAugmentDescriptions(map);
        map.put(AugmentSensitive.INSTANCE, "Places water at a target entity's feet.");
    }

    @Override
    protected void addDefaultAugmentLimits(Map<ResourceLocation, Integer> defaults) {
        super.addDefaultAugmentLimits(defaults);
        defaults.put(AugmentSensitive.INSTANCE.getRegistryName(), 1);
    }

    @Override
    public String getBookDescription() {
        return "Places water at a location or extinguishes entities on fire. If augmented with extend time, it will keep entities wet for a longer period of time.";
    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.TWO;
    }

    @NotNull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.ELEMENTAL_WATER);
    }


    @Override
    public int getBaseDuration() {
        return POTION_TIME == null ? 30 : POTION_TIME.get();
    }

    @Override
    public int getExtendTimeDuration() {
        return EXTEND_TIME == null ? 10 : EXTEND_TIME.get();
    }

    @Override
    public void buildConfig(ModConfigSpec.Builder builder) {
        super.buildConfig(builder);
        addPotionConfig(builder, 20);
        addExtendTimeConfig(builder, 10);
    }
}
