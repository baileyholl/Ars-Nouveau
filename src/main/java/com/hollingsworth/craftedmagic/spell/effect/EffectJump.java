package com.hollingsworth.craftedmagic.spell.effect;

import com.hollingsworth.craftedmagic.ModConfig;
import com.hollingsworth.craftedmagic.spell.augment.AugmentEmpower;
import com.hollingsworth.craftedmagic.spell.augment.AugmentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;

public class EffectJump extends EffectType{
    public EffectJump() {
        super(ModConfig.EffectJumpID, "Jump");
    }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, LivingEntity shooter, ArrayList<AugmentType> augments) {
        if(rayTraceResult instanceof EntityRayTraceResult){
            System.out.println("Jumping");
            LivingEntity entity = (LivingEntity) ((EntityRayTraceResult) rayTraceResult).getEntity();
            Vec3d vec3d = entity.getMotion();
            System.out.println(vec3d);
            System.out.println(entity);
            entity.setMotion(vec3d.x,
                    .75 + .75 * getBuffCount(augments, AugmentEmpower.class), vec3d.z);
            entity.velocityChanged = true;
        }
    }

    @Override
    public int getManaCost() {
        return 15;
    }
}
