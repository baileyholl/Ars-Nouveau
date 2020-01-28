package com.hollingsworth.craftedmagic.spell.effect;

import com.hollingsworth.craftedmagic.ModConfig;
import com.hollingsworth.craftedmagic.spell.augment.AugmentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import java.util.ArrayList;

public class EffectKnockback extends EffectType{

    public EffectKnockback() {
        super(ModConfig.EffectKnockbackID, "Knockback");
    }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, LivingEntity shooter, ArrayList<AugmentType> enhancements) {
        if(rayTraceResult instanceof EntityRayTraceResult){
            if(((EntityRayTraceResult) rayTraceResult).getEntity() instanceof LivingEntity){
                LivingEntity entity = (LivingEntity) ((EntityRayTraceResult) rayTraceResult).getEntity();
                entity.knockBack(shooter, (float)3 * 0.5F, (double) MathHelper.sin(shooter.rotationYaw * ((float)Math.PI / 180F)), (double)(-MathHelper.cos(shooter.rotationYaw * ((float)Math.PI / 180F))));
            }
        }
    }

    @Override
    public int getManaCost() {
        return 0;
    }
}
