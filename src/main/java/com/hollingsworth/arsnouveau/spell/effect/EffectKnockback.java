package com.hollingsworth.arsnouveau.spell.effect;

import com.hollingsworth.arsnouveau.ModConfig;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
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
                float strength = 1.5f + getAmplificationBonus(augments);
                knockback(target, shooter, strength);
                target.velocityChanged = true;
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
        return 15;
    }
}
