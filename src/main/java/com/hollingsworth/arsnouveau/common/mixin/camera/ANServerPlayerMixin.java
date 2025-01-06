package com.hollingsworth.arsnouveau.common.mixin.camera;

import com.hollingsworth.arsnouveau.common.util.CameraUtil;
import com.llamalad7.mixinextras.injector.WrapWithCondition;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * <a href="https://github.com/Geforce132/SecurityCraft/blob/1.18.2/src/main/java/net/geforcemods/securitycraft/mixin/camera/ServerPlayerMixin.java">...</a>
 * Makes sure the server does not move the player viewing a camera to the camera's position
 */
@Mixin(value = ServerPlayer.class, priority = 1000)
public class ANServerPlayerMixin {
    @WrapWithCondition(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;absMoveTo(DDDFF)V"))
    private boolean shouldMove(ServerPlayer player, double x, double y, double z, float yaw, float pitch) {
        return !CameraUtil.isPlayerMountedOnCamera(player);
    }


}