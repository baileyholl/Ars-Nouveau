package com.hollingsworth.craftedmagic.spell.effect;

import com.hollingsworth.craftedmagic.ModConfig;
import com.hollingsworth.craftedmagic.api.spell.AbstractEffect;
import com.hollingsworth.craftedmagic.spell.augment.AugmentAmplify;
import com.hollingsworth.craftedmagic.api.spell.AugmentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import java.util.ArrayList;

public class EffectHeal extends AbstractEffect {
    public EffectHeal() {
        super(ModConfig.EffectHealID, "Heal");
    }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, LivingEntity shooter, ArrayList<AugmentType> augments) {
        if(rayTraceResult instanceof EntityRayTraceResult){
            if(((EntityRayTraceResult) rayTraceResult).getEntity() instanceof LivingEntity){
                LivingEntity entity = ((LivingEntity) ((EntityRayTraceResult) rayTraceResult).getEntity());
                float maxHealth = entity.getMaxHealth();
                float healVal = 3.0f + 3 * getBuffCount(augments, AugmentAmplify.class);
                if(entity.getHealth() + healVal > maxHealth){
                    entity.setHealth(entity.getMaxHealth());
                }else{
                    entity.setHealth(entity.getHealth() + healVal);
                }
            }
        }
    }

    @Override
    public int getManaCost() {
        return 30;
    }
}
