package com.hollingsworth.arsnouveau.common.mixin;

import com.hollingsworth.arsnouveau.client.SkyTextureHandler;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(method = "resize", at = @At(value = "TAIL"))
    public void arsNouveau$resize(int width, int height, CallbackInfo ci) {
        SkyTextureHandler.setupRenderTarget(width, height);
    }
}