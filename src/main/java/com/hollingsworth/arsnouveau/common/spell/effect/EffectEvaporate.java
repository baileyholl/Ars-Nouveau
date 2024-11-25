package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.items.curios.ShapersFocus;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentPierce;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;

public class EffectEvaporate extends AbstractEffect {

    public static EffectEvaporate INSTANCE = new EffectEvaporate();

    private EffectEvaporate() {
        super(GlyphLib.EffectEvaporate, "Evaporate");
    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        BlockPos pos = rayTraceResult.getBlockPos();
        for (BlockPos p : SpellUtil.calcAOEBlocks(shooter, pos, rayTraceResult, spellStats.getAoeMultiplier(), spellStats.getBuffCount(AugmentPierce.INSTANCE))) {
            evaporate(world, p, rayTraceResult, shooter, spellContext, resolver);
            for (Direction d : Direction.values()) {
                evaporate(world, p.relative(d), rayTraceResult, shooter, spellContext, resolver);
            }
        }
    }

    public void evaporate(Level world, BlockPos p, BlockHitResult rayTraceResult, LivingEntity shooter, SpellContext context, SpellResolver resolver) {
        BlockState state = world.getBlockState(p);
        if (!world.getFluidState(p).isEmpty() && state.getBlock() instanceof LiquidBlock) {
            world.setBlock(p, Blocks.AIR.defaultBlockState(), 3);
            ShapersFocus.tryPropagateBlockSpell(new BlockHitResult(
                    new Vec3(p.getX(), p.getY(), p.getZ()), rayTraceResult.getDirection(), p, false
            ), world, shooter, context, resolver);
        } else if (state.getBlock() instanceof SimpleWaterloggedBlock && state.hasProperty(BlockStateProperties.WATERLOGGED)) {
            world.setBlock(p, state.setValue(BlockStateProperties.WATERLOGGED, Boolean.FALSE), 3);
            ShapersFocus.tryPropagateBlockSpell(new BlockHitResult(
                    new Vec3(p.getX(), p.getY(), p.getZ()), rayTraceResult.getDirection(), p, false
            ), world, shooter, context, resolver);
        }
    }

    @Override
    public String getBookDescription() {
        return "Deletes fluids in an area. Can be expanded with AOE.";
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

    @NotNull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return setOf(AugmentAOE.INSTANCE, AugmentPierce.INSTANCE);
    }

    @Override
    public void addAugmentDescriptions(Map<AbstractAugment, String> map) {
        super.addAugmentDescriptions(map);
        addBlockAoeAugmentDescriptions(map);
    }
}
