package com.hollingsworth.craftedmagic.spell.effect;

import com.hollingsworth.craftedmagic.ModConfig;
import com.hollingsworth.craftedmagic.spell.enhancement.EnhancementType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import java.util.ArrayList;

public class EffectDig extends EffectType {

    public EffectDig() {
        super(ModConfig.EffectDigID);
    }

    @Override
    public int getManaCost() {
        return 0;
    }


    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, EntityLivingBase shooter, ArrayList<EnhancementType> enhancements) {
        if(!world.isRemote){
            System.out.println("Destroying block");
            BlockPos position = rayTraceResult.getBlockPos();
            if(position != null) {
                BlockPos pos = new BlockPos(position.getX(), position.getY(), position.getZ());
                System.out.println(pos);
                IBlockState state = world.getBlockState(pos);
                //Iron block and lower.
                System.out.println(state.getBlockHardness(world, pos));

                if (state.getBlockHardness(world, pos) <= 3 && state.getBlockHardness(world, pos) >= 0) {
                    world.destroyBlock(pos, true);
                }
            }
        }

    }
}
