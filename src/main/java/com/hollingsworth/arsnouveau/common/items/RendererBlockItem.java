package com.hollingsworth.arsnouveau.common.items;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.world.item.Item.Properties;

public abstract class RendererBlockItem extends AnimBlockItem {


    public RendererBlockItem(Block block, Properties props) {
        super(block, props);
    }

    public abstract Supplier<BlockEntityWithoutLevelRenderer> getRenderer();

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        super.initializeClient(consumer);
        consumer.accept(new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
                return getRenderer().get();
            }
        });
    }
}
