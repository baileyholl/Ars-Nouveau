package com.hollingsworth.arsnouveau.common.util;

import com.hollingsworth.arsnouveau.common.entity.ICameraCallback;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class CameraUtil {
    public static boolean isPlayerMountedOnCamera(LivingEntity entity) {
        if (entity instanceof Player player) {
            return player.level.isClientSide ? ClientCameraUtil.isPlayerMountedOnCamera() : ((ServerPlayer) player).getCamera() instanceof ICameraCallback;
        } else {
            return false;
        }
    }

    public static Entity getCamera(LivingEntity entity){
        if (entity instanceof Player player) {
            return player.level.isClientSide ? ClientCameraUtil.getCamera() : ((ServerPlayer) player).getCamera();
        } else {
            return null;
        }
    }

}
