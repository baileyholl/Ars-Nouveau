package com.hollingsworth.arsnouveau.common.mixin;

import com.hollingsworth.arsnouveau.client.renderer.layer.GeoShoulderLayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
public class AddLayerPlayerMixin {

    @Inject(method = "<init>", at = @At("TAIL"))
    public void constructor(EntityRendererProvider.Context pContext, boolean pUseSlimModel, CallbackInfo ci) {
        //noinspection DataFlowIssue
        ((PlayerRenderer) (Object) this).addLayer(new GeoShoulderLayer<>((PlayerRenderer) (Object) this, pContext));
    }

}
