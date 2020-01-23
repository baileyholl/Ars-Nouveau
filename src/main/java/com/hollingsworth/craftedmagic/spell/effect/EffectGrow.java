package com.hollingsworth.craftedmagic.spell.effect;

import com.hollingsworth.craftedmagic.ModConfig;
import com.hollingsworth.craftedmagic.spell.enhancement.EnhancementType;
import net.minecraft.block.BlockState;
import net.minecraft.block.IGrowable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import java.util.ArrayList;

public class EffectGrow  extends EffectType{

    public EffectGrow() {
        super(ModConfig.EffectGrowID, "Grow");
    }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, LivingEntity shooter, ArrayList<EnhancementType> enhancements) {
        if(rayTraceResult instanceof BlockRayTraceResult) {
            BlockPos blockpos = ((BlockRayTraceResult) rayTraceResult).getPos();
            BlockPos blockpos1 = blockpos.offset(((BlockRayTraceResult) rayTraceResult).getFace());
            if (applyBonemeal(world, blockpos)) {
                if (!world.isRemote) {
                    world.playEvent(2005, blockpos, 0);
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
        return 0;
    }
}
