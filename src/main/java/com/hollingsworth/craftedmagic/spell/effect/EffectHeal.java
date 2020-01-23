package com.hollingsworth.craftedmagic.spell.effect;

import com.hollingsworth.craftedmagic.ModConfig;
import com.hollingsworth.craftedmagic.spell.enhancement.EnhancementType;
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
    public void onResolve(RayTraceResult rayTraceResult, World world, LivingEntity shooter, ArrayList<EnhancementType> enhancements) {
        if(rayTraceResult instanceof EntityRayTraceResult){
            if(((EntityRayTraceResult) rayTraceResult).getEntity() instanceof LivingEntity){
                LivingEntity entity = ((LivingEntity) ((EntityRayTraceResult) rayTraceResult).getEntity());
                float maxHealth = entity.getMaxHealth();
                if(entity.getHealth() + 3.0f > maxHealth){
                    entity.setHealth(entity.getMaxHealth());
                }else{
                    entity.setHealth(entity.getHealth() + 3.0f);
                }
            }
        }
    }

    @Override
    public int getManaCost() {
        return 0;
    }
}
