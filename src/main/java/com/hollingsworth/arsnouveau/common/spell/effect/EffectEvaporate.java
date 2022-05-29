package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentPierce;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public class EffectEvaporate extends AbstractEffect {

    public static EffectEvaporate INSTANCE = new EffectEvaporate();

    private EffectEvaporate() {
        super(GlyphLib.EffectEvaporate, "Evaporate");
    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        super.onResolveBlock(rayTraceResult, world, shooter, spellStats, spellContext);
        BlockPos pos = rayTraceResult.getBlockPos();
        for(BlockPos p : SpellUtil.calcAOEBlocks(shooter, pos, rayTraceResult, spellStats.getAoeMultiplier(), spellStats.getBuffCount(AugmentPierce.INSTANCE))){
            evaporate(world, p);
            for(Direction d : Direction.values()){
                evaporate(world, p.relative(d));
            }
        }
    }

    public void evaporate(Level world, BlockPos p){
        if(!world.getFluidState(p).isEmpty() && world.getBlockState(p).getBlock() instanceof LiquidBlock){
            world.setBlock(p, Blocks.AIR.defaultBlockState(), 3);
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

    @Nonnull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.MANIPULATION);
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return setOf(AugmentAOE.INSTANCE);
    }
}
