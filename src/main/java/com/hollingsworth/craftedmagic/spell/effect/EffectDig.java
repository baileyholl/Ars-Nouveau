package com.hollingsworth.craftedmagic.spell.effect;

import com.hollingsworth.craftedmagic.ModConfig;
import com.hollingsworth.craftedmagic.spell.augment.AugmentType;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import java.util.ArrayList;

public class EffectDig extends EffectType {

    public EffectDig() {
        super(ModConfig.EffectDigID, "Dig");
    }

    @Override
    public int getManaCost() {
        return 0;
    }


    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, LivingEntity shooter, ArrayList<AugmentType> enhancements) {
        if(!world.isRemote && rayTraceResult instanceof BlockRayTraceResult){

            BlockPos pos = new BlockPos(((BlockRayTraceResult) rayTraceResult).getPos());
            BlockState state = world.getBlockState(pos);
            //Iron block and lower.
            if (state.getBlockHardness(world, pos) <= 3 && state.getBlockHardness(world, pos) >= 0) {
                System.out.println("Destroying");
                world.destroyBlock(pos, true);
            }

        }

    }
}
