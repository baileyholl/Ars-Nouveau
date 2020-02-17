package com.hollingsworth.craftedmagic.spell.effect;

import com.hollingsworth.craftedmagic.ModConfig;
import com.hollingsworth.craftedmagic.api.spell.AbstractEffect;
import com.hollingsworth.craftedmagic.spell.augment.AugmentExtendTime;
import com.hollingsworth.craftedmagic.api.spell.AugmentType;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import java.util.ArrayList;

public class EffectIgnite  extends AbstractEffect {

    public EffectIgnite() {
        super(ModConfig.EffectIgniteID, "Ignite");
    }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, LivingEntity shooter, ArrayList<AugmentType> augments) {
        if(rayTraceResult instanceof EntityRayTraceResult){
            int duration = 2 + 2*getBuffCount(augments, AugmentExtendTime.class);
            ((EntityRayTraceResult) rayTraceResult).getEntity().setFire(duration);
        }else if(rayTraceResult instanceof BlockRayTraceResult && world.getBlockState(((BlockRayTraceResult) rayTraceResult).getPos().up()) == Blocks.AIR.getDefaultState()){
            world.setBlockState(((BlockRayTraceResult) rayTraceResult).getPos().up(), Blocks.FIRE.getDefaultState());
        }
    }

    @Override
    public int getManaCost() {
        return 20;
    }
}
