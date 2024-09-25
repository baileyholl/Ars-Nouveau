package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.items.curios.ShapersFocus;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentPierce;
import net.minecraft.core.BlockPos;
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
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

public class EffectConjureWater extends AbstractEffect {

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
    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        double aoeBuff = spellStats.getAoeMultiplier();
        List<BlockPos> posList = SpellUtil.calcAOEBlocks(shooter, rayTraceResult.getBlockPos(), rayTraceResult, aoeBuff, spellStats.getBuffCount(AugmentPierce.INSTANCE));
        if (world.dimensionType().ultraWarm())
            return;
        for (BlockPos pos1 : posList) {
            if (!BlockUtil.destroyRespectsClaim(getPlayer(shooter, (ServerLevel) world), world, pos1))
                continue;
            if(!world.isInWorldBounds(pos1))
                continue;
            BlockState hitState = world.getBlockState(pos1);
            if (hitState.getBlock() instanceof LiquidBlockContainer liquidBlockContainer && liquidBlockContainer.canPlaceLiquid(getPlayer(shooter, (ServerLevel) world), world, pos1, world.getBlockState(pos1), Fluids.WATER)) {
                liquidBlockContainer.placeLiquid(world, pos1, hitState, Fluids.WATER.getSource(true));
                ShapersFocus.tryPropagateBlockSpell(new BlockHitResult(
                        new Vec3(pos1.getX(), pos1.getY(), pos1.getZ()), rayTraceResult.getDirection(), pos1, false
                ), world, shooter, spellContext, resolver);
            } else if (world.getBlockState(pos1.relative(rayTraceResult.getDirection())).canBeReplaced(Fluids.WATER)) {
                pos1 = pos1.relative(rayTraceResult.getDirection());
                world.setBlockAndUpdate(pos1, Blocks.WATER.defaultBlockState());
                ShapersFocus.tryPropagateBlockSpell(new BlockHitResult(
                        new Vec3(pos1.getX(), pos1.getY(), pos1.getZ()), rayTraceResult.getDirection(), pos1, false
                ), world, shooter, spellContext, resolver);
            }
        }
    }

    @Override
    public int getDefaultManaCost() {
        return 80;
    }

   @NotNull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentAOE.INSTANCE, AugmentPierce.INSTANCE);
    }

    @Override
    public String getBookDescription() {
        return "Places water at a location or extinguishes entities on fire.";
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
}
