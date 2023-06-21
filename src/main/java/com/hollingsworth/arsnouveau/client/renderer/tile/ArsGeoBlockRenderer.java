package com.hollingsworth.arsnouveau.client.renderer.tile;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public abstract class ArsGeoBlockRenderer<T extends BlockEntity & GeoBlockEntity> extends GeoBlockRenderer<T> {
    public ArsGeoBlockRenderer(BlockEntityRendererProvider.Context rendererProvider, GeoModel<T> modelProvider) {
        super(modelProvider);
    }
}
