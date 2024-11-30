package com.hollingsworth.arsnouveau.common.util;

import com.hollingsworth.arsnouveau.common.entity.ICameraCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;

public class ClientCameraUtil {
    public static boolean isPlayerMountedOnCamera() {
        return Minecraft.getInstance().cameraEntity instanceof ICameraCallback;
    }

    public static Entity getCamera(){
        return Minecraft.getInstance().cameraEntity;
    }
}
