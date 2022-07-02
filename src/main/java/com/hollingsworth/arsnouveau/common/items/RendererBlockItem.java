package com.hollingsworth.arsnouveau.common.items;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.IItemRenderProperties;

import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class RendererBlockItem extends AnimBlockItem {


    public RendererBlockItem(Block block, Properties props) {
        super(block, props);
    }

    public abstract Supplier<BlockEntityWithoutLevelRenderer> getRenderer();

    @Override
    public void initializeClient(Consumer<IItemRenderProperties> consumer) {
        super.initializeClient(consumer);
        consumer.accept(new IItemRenderProperties() {
            @Override
            public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
                return getRenderer().get();
            }
        });
    }
}
