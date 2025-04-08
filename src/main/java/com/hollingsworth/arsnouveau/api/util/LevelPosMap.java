package com.hollingsworth.arsnouveau.api.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;

public class LevelPosMap {
    public Map<String, Set<BlockPos>> posMap = new ConcurrentHashMap<>();
    public BiFunction<Level, BlockPos, Boolean> removeFunction;

    public LevelPosMap(BiFunction<Level, BlockPos, Boolean> removeFunction) {
        this.removeFunction = removeFunction;
    }

    public void addPosition(Level world, BlockPos pos) {
        String key = world.dimension().location().toString();
        if (!posMap.containsKey(key))
            posMap.put(key, BlockPosSet.newHashSet());

        posMap.get(key).add(pos);
    }

    public void applyForRange(Level level, BlockPos atPos, double distanceFrom, Function<BlockPos, Boolean> breakEarlyFunction) {
        applyForRange(level, new Vec3(atPos.getX(), atPos.getY(), atPos.getZ()), distanceFrom, breakEarlyFunction);
    }

    public void applyForRange(Level level, Vec3 atPos, double distanceFrom, Function<BlockPos, Boolean> breakEarlyFunction) {
        String key = level.dimension().location().toString();
        if (!posMap.containsKey(key)) {
            return;
        }

        Set<BlockPos> worldList = posMap.getOrDefault(key, BlockPosSet.newHashSet());
        var iter = worldList.iterator();
        while (iter.hasNext()) {
            var p = iter.next();
            if (!level.isLoaded(p)) {
                continue;
            }

            if (BlockUtil.distanceFrom(atPos, p.getCenter()) <= distanceFrom) {
                if (removeFunction.apply(level, p)) {
                    iter.remove();
                } else if (breakEarlyFunction.apply(p)) {
                    break;
                }
            }
        }
    }
}
