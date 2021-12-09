package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.client.renderer.item.GenericItemRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

public class GenericRenderer extends GeoBlockRenderer {

    public static GenericModel model = new GenericModel("source_relay");

    public GenericRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn, String loc) {
        super(rendererDispatcherIn, new GenericModel(loc));
    }

    public static Supplier<BlockEntityWithoutLevelRenderer> getISTER(String loc){
        return () -> new GenericItemRenderer(new GenericModel(loc));
    }
}
