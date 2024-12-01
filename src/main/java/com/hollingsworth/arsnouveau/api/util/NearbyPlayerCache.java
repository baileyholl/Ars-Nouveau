package com.hollingsworth.arsnouveau.api.util;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NearbyPlayerCache {
    /**
     * A cache of the next time we should check for players at a given position in a given level
     */
    private static final Map<String, Cache<Long, CacheResult>> levelPlayerCache = new ConcurrentHashMap<>();

    public static boolean isPlayerNearby(BlockPos pos, ServerLevel level){
        return isPlayerNearby(pos, level, 16);
    }

    public static boolean isPlayerNearby(BlockPos pos, ServerLevel level, int range){
        String key = level.dimension().location().toString();
        if (!levelPlayerCache.containsKey(key))
            levelPlayerCache.put(key, CacheBuilder.newBuilder().maximumSize(1000).build());
        Cache<Long, CacheResult> positionCache = levelPlayerCache.get(key);
        long posLong = pos.asLong();
        CacheResult playerResult = positionCache.getIfPresent(posLong);
        long gameTime = level.getGameTime();

        if(playerResult != null && playerResult.nextCheck > gameTime){
            return playerResult.isPlayerNearby();
        }

        long nextCheck = gameTime + 500 + level.random.nextInt(20);
        
        for(Player player : level.players()){
            if(BlockUtil.distanceFrom(player.blockPosition(), pos) < range){
                positionCache.put(posLong, new CacheResult(true, nextCheck));
                return true;
            }
        }
        positionCache.put(posLong, new CacheResult(false, nextCheck));
        return false;
    }

    private record CacheResult(boolean isPlayerNearby, long nextCheck) { }

}
