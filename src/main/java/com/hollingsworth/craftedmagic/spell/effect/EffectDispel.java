package com.hollingsworth.craftedmagic.spell.effect;

import com.google.gson.stream.JsonToken;
import com.hollingsworth.craftedmagic.ModConfig;
import com.hollingsworth.craftedmagic.spell.enhancement.EnhancementType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collection;

public class EffectDispel extends EffectType{
    public EffectDispel() {
        super(ModConfig.EffectDispelID, "Dispel");
    }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, LivingEntity shooter, ArrayList<EnhancementType> enhancements) {
        if(rayTraceResult instanceof EntityRayTraceResult){
            if(((EntityRayTraceResult) rayTraceResult).getEntity() instanceof LivingEntity){
                LivingEntity entity = (LivingEntity) ((EntityRayTraceResult) rayTraceResult).getEntity();
                Collection<EffectInstance> effects =  entity.getActivePotionEffects();
                for(EffectInstance e : effects){
                    System.out.println(e);
                    entity.removePotionEffect(e.getPotion());
                }
                System.out.println("Done dispelling");
            }
        }
    }

    @Override
    public int getManaCost() {
        return 0;
    }
}
