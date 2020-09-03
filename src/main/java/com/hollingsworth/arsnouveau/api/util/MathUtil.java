package com.hollingsworth.arsnouveau.api.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

import java.util.List;

public class MathUtil {
// https://github.com/Mithion/ArsMagica2/tree/6d6b68002363b2569c2f2300c8f9146ad800bbc6#readme
    public static Vec3d getEntityLookHit(LivingEntity entity, double range){
        float var4 = 1.0F;
        float var5 = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * var4;
        float var6 = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * var4;
        double var7 = entity.prevPosX + (entity.getPosX() - entity.prevPosX) * var4;
        double var9 = entity.prevPosY + (entity.getPosY() - entity.prevPosY) * var4 + 1.6D - entity.getYOffset();
        double var11 = entity.prevPosZ + (entity.getPosZ() - entity.prevPosZ) * var4;
        Vec3d var13 = new Vec3d(var7, var9, var11);
        float var14 = MathHelper.cos(-var6 * 0.017453292F - (float)Math.PI);
        float var15 = MathHelper.sin(-var6 * 0.017453292F - (float)Math.PI);
        float var16 = -MathHelper.cos(-var5 * 0.017453292F);
        float var17 = MathHelper.sin(-var5 * 0.017453292F);
        float var18 = var15 * var16;
        float var20 = var14 * var16;

        return var13.add(var18 * range, var17 * range, var20 * range);
    }

    public static EntityRayTraceResult getLookedAtEntity(LivingEntity entity,int range){
        Vec3d vec3d = entity.getEyePosition(1.0f);
        Vec3d vec3d1 = entity.getLook(1.0F);
        Vec3d vec3d2 = vec3d.add(vec3d1.x * range, vec3d1.y * range, vec3d1.z * range);
        float f = 1.0F;
        AxisAlignedBB axisalignedbb = entity.getBoundingBox().expand(vec3d1.scale(range)).grow(1.0D, 1.0D, 1.0D);
        EntityRayTraceResult entityraytraceresult = ProjectileHelper.rayTraceEntities(entity, vec3d, vec3d2, axisalignedbb, (p_215312_0_) -> {
            return !p_215312_0_.isSpectator() && p_215312_0_.canBeCollidedWith();
        }, range);
        return entityraytraceresult;
    }
}
