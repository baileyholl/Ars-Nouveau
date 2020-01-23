package com.hollingsworth.craftedmagic.spell.effect;

import com.hollingsworth.craftedmagic.ModConfig;
import com.hollingsworth.craftedmagic.spell.enhancement.EnhancementType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import java.util.ArrayList;

public class EffectPhantomBlock extends EffectType{

    public EffectPhantomBlock() {
        super(ModConfig.EffectPhantomBlockID, "Phantom");
    }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, LivingEntity shooter, ArrayList<EnhancementType> enhancements) {
        if(rayTraceResult instanceof BlockRayTraceResult){
            System.out.println(((BlockRayTraceResult) rayTraceResult).getPos().offset(((BlockRayTraceResult) rayTraceResult).getFace()));
            BlockPos pos = ((BlockRayTraceResult) rayTraceResult).getPos().offset(((BlockRayTraceResult) rayTraceResult).getFace());
            if(world.getBlockState(pos).getBlock() == Blocks.AIR){
                world.setBlockState(pos, Blocks.COBBLESTONE.getDefaultState());
            }
        }
    }

    @Override
    public int getManaCost() {
        return 0;
    }
}
