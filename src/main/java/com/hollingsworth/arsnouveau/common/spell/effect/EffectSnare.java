package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.ModConfig;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import java.util.List;

public class EffectSnare extends AbstractEffect {

    public EffectSnare() {
        super(ModConfig.EffectSnareID, "Snare");
    }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments) {
        if(rayTraceResult instanceof EntityRayTraceResult){
            Entity livingEntity = ((EntityRayTraceResult) rayTraceResult).getEntity();
            if(!(livingEntity instanceof LivingEntity))
                return;
            ((LivingEntity)livingEntity).addPotionEffect(new EffectInstance(Effects.SLOWNESS,  200, 20));
            livingEntity.setMotion(0,0,0);
            livingEntity.velocityChanged = true;
        }
    }

    @Override
    public int getManaCost() {
        return 0;
    }
}
