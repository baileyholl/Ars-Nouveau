package com.hollingsworth.craftedmagic.spell.effect;

import com.hollingsworth.craftedmagic.ModConfig;
import com.hollingsworth.craftedmagic.api.spell.AbstractEffect;
import com.hollingsworth.craftedmagic.api.spell.AbstractAugment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import java.util.ArrayList;

public class EffectKnockback extends AbstractEffect {

    public EffectKnockback() {
        super(ModConfig.EffectKnockbackID, "Knockback");
    }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, LivingEntity shooter, ArrayList<AbstractAugment> augments) {
        if(rayTraceResult instanceof EntityRayTraceResult){

            if(((EntityRayTraceResult) rayTraceResult).getEntity() instanceof LivingEntity){
                LivingEntity target = (LivingEntity) ((EntityRayTraceResult) rayTraceResult).getEntity();
                int strength = 3 + getAmplificationBonus(augments);
                knockback(target, shooter, strength);
            }
        }
    }

    public void knockback(LivingEntity target, LivingEntity entityKnockingAway, float strength){
        target.knockBack(entityKnockingAway, strength, (double) MathHelper.sin(entityKnockingAway.rotationYaw * ((float)Math.PI / 180F)), (double)(-MathHelper.cos(entityKnockingAway.rotationYaw * ((float)Math.PI / 180F))));
    }

    @Override
    public boolean dampenIsAllowed() {
        return true;
    }

    @Override
    public int getManaCost() {
        return 10;
    }
}
