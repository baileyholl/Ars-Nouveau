package com.hollingsworth.craftedmagic.spell.effect;

import com.hollingsworth.craftedmagic.ModConfig;
import com.hollingsworth.craftedmagic.spell.augment.AugmentEmpower;
import com.hollingsworth.craftedmagic.spell.augment.AugmentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import java.util.ArrayList;

public class EffectHeal extends EffectType{
    public EffectHeal() {
        super(ModConfig.EffectHealID, "Heal");
    }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, LivingEntity shooter, ArrayList<AugmentType> augments) {
        if(rayTraceResult instanceof EntityRayTraceResult){
            if(((EntityRayTraceResult) rayTraceResult).getEntity() instanceof LivingEntity){
                LivingEntity entity = ((LivingEntity) ((EntityRayTraceResult) rayTraceResult).getEntity());
                float maxHealth = entity.getMaxHealth();
                float healVal = 3.0f + 3 * getBuffCount(augments, AugmentEmpower.class);
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
