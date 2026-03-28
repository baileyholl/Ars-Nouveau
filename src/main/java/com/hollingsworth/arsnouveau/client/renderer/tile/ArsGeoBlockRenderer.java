package com.hollingsworth.arsnouveau.client.renderer.tile;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

// GeckoLib 5.4.2: GeoBlockRenderer requires R extends BlockEntityRenderState & GeoRenderState.
// ArsBlockEntityRenderState satisfies this at compile time; GeckoLib's mixin does it at runtime.
public abstract class ArsGeoBlockRenderer<T extends BlockEntity & GeoBlockEntity> extends GeoBlockRenderer<T, ArsBlockEntityRenderState> {
    public ArsGeoBlockRenderer(BlockEntityRendererProvider.Context rendererProvider, GeoModel<T> modelProvider) {
        super(modelProvider);
    }

    @Override
    public ArsBlockEntityRenderState createRenderState() {
        return new ArsBlockEntityRenderState();
    }
}
