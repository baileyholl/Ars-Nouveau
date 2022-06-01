package com.hollingsworth.arsnouveau.common.mixin.camera;

import com.hollingsworth.arsnouveau.common.util.CameraUtil;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * https://github.com/Geforce132/SecurityCraft/blob/1.18.2/src/main/java/net/geforcemods/securitycraft/mixin/camera/ServerPlayerMixin.java
 * Makes sure the server does not move the player viewing a camera to the camera's position
 */
@Mixin(value = ServerPlayer.class, priority = 1000)
public class ANServerPlayerMixin {
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;absMoveTo(DDDFF)V"))
    private void an_tick(ServerPlayer player, double x, double y, double z, float yaw, float pitch) {
        System.out.println("fire");
        if (!CameraUtil.isPlayerMountedOnCamera(player)) {
            System.out.println("move to");
            player.absMoveTo(x, y, z, yaw, pitch);
        }
    }
}