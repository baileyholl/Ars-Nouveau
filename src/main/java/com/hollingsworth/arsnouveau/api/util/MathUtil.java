package com.hollingsworth.arsnouveau.api.util;


import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;
import java.util.function.Predicate;

public class MathUtil {
// https://github.com/Mithion/ArsMagica2/tree/6d6b68002363b2569c2f2300c8f9146ad800bbc6#readme

    public static EntityHitResult getLookedAtEntity(LivingEntity entity,int range){
        Vec3 vec3d = entity.getEyePosition(1.0f);
        Vec3 vec3d1 = entity.getViewVector(1.0F);
        Vec3 vec3d2 = vec3d.add(vec3d1.x * range, vec3d1.y * range, vec3d1.z * range);
        AABB axisalignedbb = entity.getBoundingBox().expandTowards(vec3d1.scale(range)).inflate(1.0D, 1.0D, 1.0D);
        return MathUtil.traceEntities(entity, vec3d, vec3d2, axisalignedbb, (e) -> !e.isSpectator() && e.isPickable(), range);
    }

    public static EntityHitResult traceEntities(Entity shooter, Vec3 startVec, Vec3 endVec, AABB boundingBox, Predicate<Entity> filter, double distance){
        Level world = shooter.level;
        double d0 = distance;
        Entity entity = null;
        Vec3 vec3d = null;

        for(Entity entity1 : world.getEntities(shooter, boundingBox, filter)) {
            AABB axisalignedbb = entity1.getBoundingBox().inflate(entity1.getPickRadius());
            Optional<Vec3> optional = axisalignedbb.clip(startVec, endVec);
            if (axisalignedbb.contains(startVec)) {
                if (d0 >= 0.0D) {
                    entity = entity1;
                    vec3d = optional.orElse(startVec);
                    d0 = 0.0D;
                }
            } else if (optional.isPresent()) {
                Vec3 vec3d1 = optional.get();
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

        return entity == null ? null : new EntityHitResult(entity, vec3d);
    }

    public static long NIGHT_TIME = 13000L;
    public static long DAY_TIME = 1000L;

    public static long getNextDaysTime(Level world, long timeOfDay){
        long lengthOfDay = 24000L;
        long dayTime = world.getDayTime();
        long newTime = (dayTime + lengthOfDay);
        newTime -= newTime % lengthOfDay;
        return newTime + timeOfDay;
    }
}
