package com.hollingsworth.arsnouveau.api.util;


import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class MathUtil {
// https://github.com/Mithion/ArsMagica2/tree/6d6b68002363b2569c2f2300c8f9146ad800bbc6#readme
    public static Vector3d getEntityLookHit(LivingEntity entity, double range) {
        float var4 = 1.0F;
        float var5 = entity.xRotO + (entity.xRot - entity.xRotO) * var4;
        float var6 = entity.yRotO + (entity.yRot - entity.yRotO) * var4;
        double var7 = entity.xo + (entity.getX() - entity.xo) * var4;
        double var9 = entity.yo + (entity.getY() - entity.yo) * var4 + 1.6D - entity.getMyRidingOffset();
        double var11 = entity.zo + (entity.getZ() - entity.zo) * var4;
        Vector3d var13 = new Vector3d(var7, var9, var11);
        float var14 = MathHelper.cos(-var6 * 0.017453292F - (float) Math.PI);
        float var15 = MathHelper.sin(-var6 * 0.017453292F - (float) Math.PI);
        float var16 = -MathHelper.cos(-var5 * 0.017453292F);
        float var17 = MathHelper.sin(-var5 * 0.017453292F);
        float var18 = var15 * var16;
        float var20 = var14 * var16;

        return var13.add(var18 * range, var17 * range, var20 * range);
    }

    public static EntityRayTraceResult getLookedAtEntity(LivingEntity entity,int range){
        Vector3d vec3d = entity.getEyePosition(1.0f);
        Vector3d vec3d1 = entity.getViewVector(1.0F);
        Vector3d vec3d2 = vec3d.add(vec3d1.x * range, vec3d1.y * range, vec3d1.z * range);
        float f = 1.0F;
        AxisAlignedBB axisalignedbb = entity.getBoundingBox().expandTowards(vec3d1.scale(range)).inflate(1.0D, 1.0D, 1.0D);
        EntityRayTraceResult entityraytraceresult = MathUtil.traceEntities(entity, vec3d, vec3d2, axisalignedbb, (p_215312_0_) -> {
            return !p_215312_0_.isSpectator() && p_215312_0_.isPickable();
        }, range);
        return entityraytraceresult;
    }
    // ProjectileHelper#TraceEntities
    // Fuck mojang why the hell is this client only
    public static EntityRayTraceResult traceEntities(Entity shooter, Vector3d startVec, Vector3d endVec, AxisAlignedBB boundingBox, Predicate<Entity> filter, double distance){
        World world = shooter.level;
        double d0 = distance;
        Entity entity = null;
        Vector3d vec3d = null;

        for(Entity entity1 : world.getEntities(shooter, boundingBox, filter)) {
            AxisAlignedBB axisalignedbb = entity1.getBoundingBox().inflate((double)entity1.getPickRadius());
            Optional<Vector3d> optional = axisalignedbb.clip(startVec, endVec);
            if (axisalignedbb.contains(startVec)) {
                if (d0 >= 0.0D) {
                    entity = entity1;
                    vec3d = optional.orElse(startVec);
                    d0 = 0.0D;
                }
            } else if (optional.isPresent()) {
                Vector3d vec3d1 = optional.get();
                double d1 = startVec.distanceToSqr(vec3d1);
                if (d1 < d0 || d0 == 0.0D) {
                    if (entity1.getRootVehicle() == shooter.getRootVehicle() && !entity1.canRiderInteract()) {
                        if (d0 == 0.0D) {
                            entity = entity1;
                            vec3d = vec3d1;
                        }
                    } else {
                        entity = entity1;
                        vec3d = vec3d1;
                        d0 = d1;
                    }
                }
            }
        }

        return entity == null ? null : new EntityRayTraceResult(entity, vec3d);
    }
}
