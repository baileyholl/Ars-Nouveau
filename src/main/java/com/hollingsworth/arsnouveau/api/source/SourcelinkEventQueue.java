package com.hollingsworth.arsnouveau.api.source;

import com.hollingsworth.arsnouveau.common.block.tile.SourcelinkTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.Event;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SourcelinkEventQueue {

    public static Map<String, Set<BlockPos>> posMap = new ConcurrentHashMap<>();

    public static void addPosition(Level world, BlockPos pos) {
        String key = world.dimension().location().toString();
        if (!posMap.containsKey(key))
            posMap.put(key, new HashSet<>());

        posMap.get(key).add(pos);
    }

    public static void addManaEvent(Level world, Class<? extends SourcelinkTile> tileType, int amount, Event event, BlockPos sourcePos) {
        List<BlockPos> stalePos = new ArrayList<>();
        Set<BlockPos> worldList = posMap.getOrDefault(world.dimension().location().toString(), new HashSet<>());
        for (BlockPos p : worldList) {
            if (!world.isLoaded(p))
                continue;
            BlockEntity entity = world.getBlockEntity(p);
            if (world.getBlockEntity(p) == null || !(entity instanceof SourcelinkTile sourcelinkTile)) {
                stalePos.add(p);
                continue;
            }
            if (entity.getClass().equals(tileType) && sourcelinkTile.eventInRange(sourcePos, event) && sourcelinkTile.canAcceptSource()) {
                sourcelinkTile.getManaEvent(sourcePos, amount);
                break;
            }
        }
        for (BlockPos p : stalePos) {
            worldList.remove(p);
        }
    }
}
