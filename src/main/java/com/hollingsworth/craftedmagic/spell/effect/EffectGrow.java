package com.hollingsworth.craftedmagic.spell.effect;

import com.hollingsworth.craftedmagic.ModConfig;
import com.hollingsworth.craftedmagic.api.spell.AbstractEffect;
import com.hollingsworth.craftedmagic.api.spell.AbstractAugment;
import com.hollingsworth.craftedmagic.api.util.SpellUtil;
import com.hollingsworth.craftedmagic.spell.augment.AugmentAOE;
import net.minecraft.block.BlockState;
import net.minecraft.block.IGrowable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import java.util.ArrayList;

public class EffectGrow  extends AbstractEffect {

    public EffectGrow() {
        super(ModConfig.EffectGrowID, "Grow");
    }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, LivingEntity shooter, ArrayList<AbstractAugment> augments) {
        if(rayTraceResult instanceof BlockRayTraceResult) {
            for(BlockPos blockpos : SpellUtil.calcAOEBlocks((PlayerEntity) shooter, ((BlockRayTraceResult) rayTraceResult).getPos(), (BlockRayTraceResult) rayTraceResult, getBuffCount(augments, AugmentAOE.class))){
                //BlockPos blockpos = ((BlockRayTraceResult) rayTraceResult).getPos();
                if (applyBonemeal(world, blockpos)) {
                    if (!world.isRemote) {
                        world.playEvent(2005, blockpos, 0);
                    }
                }
            }
        }
    }
    public static boolean applyBonemeal(World worldIn, BlockPos pos) {
        BlockState blockstate = worldIn.getBlockState(pos);
        if (blockstate.getBlock() instanceof IGrowable) {
            IGrowable igrowable = (IGrowable)blockstate.getBlock();
            if (igrowable.canGrow(worldIn, pos, blockstate, worldIn.isRemote)) {
                if (!worldIn.isRemote) {
                    if (igrowable.canUseBonemeal(worldIn, worldIn.rand, pos, blockstate)) {
                        igrowable.grow(worldIn, worldIn.rand, pos, blockstate);
                    }
                }

                return true;
            }
        }

        return false;
    }

    @Override
    public int getManaCost() {
        return 30;
    }
}
