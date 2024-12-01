package com.hollingsworth.arsnouveau.api.util;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NearbyPlayerCache {
    /**
     * A cache of the next time we should check for players at a given position in a given level
     */
    private static Map<String, Map<Long, Integer>> levelPlayerCache = new ConcurrentHashMap<>();



    public static boolean isPlayerNearby(BlockPos pos, ServerLevel level, int range){
        String key = level.dimension().location().toString();
        if (!levelPlayerCache.containsKey(key))
            levelPlayerCache.put(key, new HashMap<>());
        Map<Long, Integer> positionCache = levelPlayerCache.get(key);
        long posLong = pos.asLong();
        int nextCheck = positionCache.getOrDefault(posLong, 0);

        long gameTime = level.getGameTime();
        if(nextCheck > gameTime){
            System.out.println("Cache hit");
            return true;
        }

        for(Player player : level.players()){
            if(BlockUtil.distanceFrom(player.blockPosition(), pos) < range){
                positionCache.put(posLong, (int)gameTime + 500 + level.random.nextInt(20));
                System.out.println("found player");
                return true;
            }
        }
        System.out.println("no player nearby");
        return false;
    }

}
