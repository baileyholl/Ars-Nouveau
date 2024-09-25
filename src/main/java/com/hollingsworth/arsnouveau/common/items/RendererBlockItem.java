package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.setup.registry.BlockRegistryWrapper;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.level.block.Block;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;

import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class RendererBlockItem extends AnimBlockItem {


    public RendererBlockItem(Block block, Properties props) {
        super(block, props);
    }

    public RendererBlockItem(BlockRegistryWrapper<? extends Block> block, Properties props) {
        this(block.get(), props);
    }

    public abstract Supplier<BlockEntityWithoutLevelRenderer> getRenderer();

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            @Override
            public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                return getRenderer().get();
            }
        });
    }
}
