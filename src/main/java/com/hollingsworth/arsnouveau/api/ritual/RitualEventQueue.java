package com.hollingsworth.arsnouveau.api.ritual;

import com.hollingsworth.arsnouveau.common.block.tile.RitualBrazierTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;

public class RitualEventQueue {

    public static Map<String, Set<BlockPos>> posMap = new HashMap<>();

    public static void addPosition(Level world, BlockPos pos) {
        String key = world.dimension().location().toString();
        if (!posMap.containsKey(key))
            posMap.put(key, new HashSet<>());

        posMap.get(key).add(pos);
    }

    public static boolean containsPosition(Level world, BlockPos pos) {
        String key = world.dimension().location().toString();
        if (!posMap.containsKey(key))
            return false;

        return posMap.get(key).contains(pos);
    }

    public static <T extends RangeRitual> List<T> getRituals(Level level, Class<T> type) {
        List<T> rituals = new ArrayList<>();
        Set<BlockPos> worldList = posMap.getOrDefault(level.dimension().location().toString(), new HashSet<>());
        List<BlockPos> stalePos = new ArrayList<>();
        for (BlockPos p : worldList) {
            if (!level.isLoaded(p))
                continue;
            BlockEntity entity = level.getBlockEntity(p);
            if (!(entity instanceof RitualBrazierTile brazierTile)) {
                stalePos.add(p);
                continue;
            }
            AbstractRitual ritual = brazierTile.ritual;
            if (ritual != null && ritual.getClass().equals(type)) {
                rituals.add((T) ritual);
            }
        }
        return rituals;
    }

    /**
     * Used to run operations on the first matching ranged ritual.
     * Returns the first ritual of the given type that matches the predicate.
     */
    public static @Nullable <T extends RangeRitual> T getRitual(Level level, Class<T> type, Predicate<T> isMatch) {
        Set<BlockPos> worldList = posMap.getOrDefault(level.dimension().location().toString(), new HashSet<>());
        List<BlockPos> stalePos = new ArrayList<>();
        for (BlockPos p : worldList) {
            if (!level.isLoaded(p))
                continue;
            BlockEntity entity = level.getBlockEntity(p);
            if (!(entity instanceof RitualBrazierTile brazierTile)) {
                stalePos.add(p);
                continue;
            }
            AbstractRitual ritual = brazierTile.ritual;
            if (ritual != null && ritual.getClass().equals(type) && isMatch.test((T) ritual)) {
                return (T) ritual;
            }
        }
        for (BlockPos p : stalePos) {
            worldList.remove(p);
        }
        return null;
    }
}
