package com.hollingsworth.arsnouveau.api.util;


import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.Optional;
import java.util.function.Predicate;

public class MathUtil {
// https://github.com/Mithion/ArsMagica2/tree/6d6b68002363b2569c2f2300c8f9146ad800bbc6#readme

    public static EntityRayTraceResult getLookedAtEntity(LivingEntity entity,int range){
        Vector3d vec3d = entity.getEyePosition(1.0f);
        Vector3d vec3d1 = entity.getViewVector(1.0F);
        Vector3d vec3d2 = vec3d.add(vec3d1.x * range, vec3d1.y * range, vec3d1.z * range);
        AxisAlignedBB axisalignedbb = entity.getBoundingBox().expandTowards(vec3d1.scale(range)).inflate(1.0D, 1.0D, 1.0D);
        return MathUtil.traceEntities(entity, vec3d, vec3d2, axisalignedbb, (e) -> !e.isSpectator() && e.isPickable(), range);
    }

    public static EntityRayTraceResult traceEntities(Entity shooter, Vector3d startVec, Vector3d endVec, AxisAlignedBB boundingBox, Predicate<Entity> filter, double distance){
        World world = shooter.level;
        double d0 = distance;
        Entity entity = null;
        Vector3d vec3d = null;

        for(Entity entity1 : world.getEntities(shooter, boundingBox, filter)) {
            AxisAlignedBB axisalignedbb = entity1.getBoundingBox().inflate(entity1.getPickRadius());
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

    public static long NIGHT_TIME = 13000L;
    public static long DAY_TIME = 1000L;

    public static long getNextDaysTime(World world, long timeOfDay){
        long lengthOfDay = 24000L;
        long dayTime = world.getDayTime();
        long newTime = (dayTime + lengthOfDay);
        newTime -= newTime % lengthOfDay;
        return newTime + timeOfDay;
    }
}
