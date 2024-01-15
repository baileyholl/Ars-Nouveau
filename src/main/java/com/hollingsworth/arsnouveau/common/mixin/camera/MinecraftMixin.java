package com.hollingsworth.arsnouveau.common.mixin.camera;

import com.hollingsworth.arsnouveau.common.util.ClientCameraUtil;
import com.llamalad7.mixinextras.injector.WrapWithCondition;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Disallows players from pressing F5 (by default) to change to third person while viewing a camera
 * https://github.com/Geforce132/SecurityCraft/blob/1.18.2/src/main/java/net/geforcemods/securitycraft/mixin/camera/MinecraftMixin.java
 */
@Mixin(value = Minecraft.class, priority = 1100)
public class MinecraftMixin {
    @WrapWithCondition(method = "handleKeybinds", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Options;setCameraType(Lnet/minecraft/client/CameraType;)V"))
    private boolean arsNouveau$handleKeybinds(Options options, CameraType newType) {
       return !ClientCameraUtil.isPlayerMountedOnCamera();
    }
}
