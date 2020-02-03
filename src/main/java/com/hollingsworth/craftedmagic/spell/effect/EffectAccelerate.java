package com.hollingsworth.craftedmagic.spell.effect;

import com.hollingsworth.craftedmagic.ModConfig;
import com.hollingsworth.craftedmagic.spell.augment.AugmentEmpower;
import com.hollingsworth.craftedmagic.spell.augment.AugmentExtendTime;
import com.hollingsworth.craftedmagic.spell.augment.AugmentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import java.util.ArrayList;

public class EffectAccelerate extends EffectType{

    public EffectAccelerate() {
        super(ModConfig.EffectAccelerateID, "Accelerate");
    }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, LivingEntity shooter, ArrayList<AugmentType> augments) {
        if(rayTraceResult instanceof EntityRayTraceResult && ((EntityRayTraceResult) rayTraceResult).getEntity() instanceof LivingEntity){
            applyPotion(((LivingEntity) ((EntityRayTraceResult) rayTraceResult).getEntity()), Effects.SPEED, augments);
        }
    }

    @Override
    public int getManaCost() {
        return 10;
    }
}
