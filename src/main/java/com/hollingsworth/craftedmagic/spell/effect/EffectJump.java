package com.hollingsworth.craftedmagic.spell.effect;

import com.hollingsworth.craftedmagic.ModConfig;
import com.hollingsworth.craftedmagic.api.spell.AbstractEffect;
import com.hollingsworth.craftedmagic.spell.augment.AugmentAmplify;
import com.hollingsworth.craftedmagic.api.spell.AugmentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;

public class EffectJump extends AbstractEffect {
    public EffectJump() {
        super(ModConfig.EffectJumpID, "Jump");
    }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, LivingEntity shooter, ArrayList<AugmentType> augments) {
        if(rayTraceResult instanceof EntityRayTraceResult){
            LivingEntity entity = (LivingEntity) ((EntityRayTraceResult) rayTraceResult).getEntity();
            Vec3d vec3d = entity.getMotion();
            entity.setMotion(vec3d.x,
                    .75 + .75 * getBuffCount(augments, AugmentAmplify.class), vec3d.z);
            entity.velocityChanged = true;
        }
    }

    @Override
    public int getManaCost() {
        return 15;
    }
}
