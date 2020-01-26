package com.hollingsworth.craftedmagic.spell.effect;

import com.hollingsworth.craftedmagic.ModConfig;
import com.hollingsworth.craftedmagic.spell.enhancement.EnhancementType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import java.util.ArrayList;

public class EffectLight extends EffectType{

    public EffectLight() {
        super(ModConfig.EffectLightID, "Light");
    }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, LivingEntity shooter, ArrayList<EnhancementType> enhancements) {
        if(rayTraceResult instanceof EntityRayTraceResult){
            if(((EntityRayTraceResult) rayTraceResult).getEntity() instanceof LivingEntity){
                int seconds = 30;
                ((LivingEntity) ((EntityRayTraceResult) rayTraceResult).getEntity()).addPotionEffect(new EffectInstance(Effects.NIGHT_VISION, 20 * seconds, 0));
            }
        }
    }

    @Override
    public int getManaCost() {
        return 0;
    }
}
