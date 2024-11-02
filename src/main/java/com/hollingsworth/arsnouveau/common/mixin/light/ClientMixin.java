package com.hollingsworth.arsnouveau.common.mixin.light;

import com.hollingsworth.arsnouveau.common.light.LightManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class ClientMixin {
    @Inject(method = "updateLevelInEngines", at = @At("HEAD"))
    private void ars_nouveau$onSetWorld(ClientLevel world, CallbackInfo ci) {
        LightManager.clearLightSources();
    }
}
