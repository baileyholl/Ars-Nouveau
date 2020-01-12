package com.hollingsworth.craftedmagic.spell.effect;

import com.hollingsworth.craftedmagic.ModConfig;
import com.hollingsworth.craftedmagic.spell.enhancement.EnhancementType;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
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
    public void onResolve(RayTraceResult rayTraceResult, World world, LivingEntity shooter, ArrayList<EnhancementType> enhancements) {
        if(!world.isRemote){

            BlockPos pos = new BlockPos(rayTraceResult.getHitVec());
            BlockState state = world.getBlockState(pos);
            //Iron block and lower.
            if (state.getBlockHardness(world, pos) <= 3 && state.getBlockHardness(world, pos) >= 0) {
                System.out.println("Destroying");
                if(!world.destroyBlock(pos, true)){ // Check if we hit the top of the block.
                    world.destroyBlock(pos.down(), true);

                }
            }

        }

    }
}
