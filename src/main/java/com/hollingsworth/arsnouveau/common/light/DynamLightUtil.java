package com.hollingsworth.arsnouveau.common.light;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

import java.util.function.Function;

public class DynamLightUtil {


    public static int getSectionCoord(double coord) {
        return getSectionCoord(Mth.floor(coord));
    }

    public static int getSectionCoord(int coord) {
        return coord >> 4;
    }

    public static int getLuminance(Entity entity) {
        int level = 0;
        if (LightManager.getLightRegistry().containsKey(entity.getType())) {
            for (Function<Entity, Integer> function : LightManager.getLightRegistry().get(entity.getType())) {
                int val = function.apply(entity);
                level = Math.max(val, level);
            }
        }
        return Math.min(15, level);
    }

}
