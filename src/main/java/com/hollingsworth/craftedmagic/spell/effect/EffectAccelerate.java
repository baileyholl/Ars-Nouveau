package com.hollingsworth.craftedmagic.spell.effect;

import com.hollingsworth.craftedmagic.ModConfig;
import com.hollingsworth.craftedmagic.spell.enhancement.EnhancementType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.potion.Potion;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import java.util.ArrayList;

public class EffectAccelerate extends EffectType{

    public EffectAccelerate() {
        super(ModConfig.EffectAccelerateID, "Accelerate");
    }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, LivingEntity shooter, ArrayList<EnhancementType> enhancements) {
        if(rayTraceResult instanceof EntityRayTraceResult){
            if(((EntityRayTraceResult) rayTraceResult).getEntity() instanceof LivingEntity){
                int seconds = 30;
                ((LivingEntity) ((EntityRayTraceResult) rayTraceResult).getEntity()).addPotionEffect(new EffectInstance(Effects.SPEED, seconds * 20, 1));
            }
        }
    }

    @Override
    public int getManaCost() {
        return 0;
    }
}
