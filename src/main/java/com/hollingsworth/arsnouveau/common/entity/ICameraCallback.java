package com.hollingsworth.arsnouveau.common.entity;

import net.minecraft.server.level.ChunkTrackingView;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

public interface ICameraCallback {
    List<Player> DISMOUNTED_PLAYERS = new ArrayList<>();

    static boolean hasRecentlyDismounted(Player player) {
        return DISMOUNTED_PLAYERS.remove(player);
    }


    void onLeftPressed();

    void onRightPressed();

    void onUpPressed();

    void onDownPressed();

    void stopViewing(ServerPlayer player);

    ChunkTrackingView getCameraChunks();

    boolean shouldUpdateChunkTracking(ServerPlayer player);

    default Entity entity(){
        return (Entity) this;
    }

}
