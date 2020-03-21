package com.hollingsworth.arsnouveau.spell.effect;

import com.hollingsworth.arsnouveau.ModConfig;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collection;

public class EffectDispel extends AbstractEffect {
    public EffectDispel() {
        super(ModConfig.EffectDispelID, "Dispel");
    }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, LivingEntity shooter, ArrayList<AbstractAugment> augments) {
        if(rayTraceResult instanceof EntityRayTraceResult){
            if(((EntityRayTraceResult) rayTraceResult).getEntity() instanceof LivingEntity){
                LivingEntity entity = (LivingEntity) ((EntityRayTraceResult) rayTraceResult).getEntity();
                Collection<EffectInstance> effects =  entity.getActivePotionEffects();
                for(EffectInstance e : effects){
                    entity.removePotionEffect(e.getPotion());
                }
            }
        }
    }

    @Override
    public int getManaCost() {
        return 30;
    }
}
