package com.hollingsworth.arsnouveau.common.mixin.camera;

import com.hollingsworth.arsnouveau.common.util.CameraUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.ViewArea;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * This mixin fixes camera chunks disappearing when the player entity moves while viewing a camera (e.g. while being in a
 * minecart or falling)
 * <a href="https://github.com/Geforce132/SecurityCraft/blob/1.18.2/src/main/java/net/geforcemods/securitycraft/mixin/camera/LevelRendererMixin.java">From SecurityCraft</a>
 */
@Mixin(value = LevelRenderer.class, priority = 1100)
public class LevelRendererMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Redirect(method = "setupRender", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ViewArea;repositionCamera(DD)V"))
    public void arsNouveau$onRepositionCamera(ViewArea viewArea, double x, double z) {
        if (!CameraUtil.isPlayerMountedOnCamera(minecraft.player))
            viewArea.repositionCamera(x, z);
    }
}