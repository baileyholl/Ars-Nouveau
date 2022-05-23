package com.hollingsworth.arsnouveau.common.util;

import com.hollingsworth.arsnouveau.common.entity.ScryerCamera;
import net.minecraft.client.Minecraft;

public class ClientCameraUtil {
    public static boolean isPlayerMountedOnCamera() {
        return Minecraft.getInstance().cameraEntity instanceof ScryerCamera;
    }
}
