package com.hollingsworth.arsnouveau.setup.registry;

import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;

public class BlockRegistryWrapper<T extends Block> extends RegistryWrapper<Block, T> {
    public BlockRegistryWrapper(DeferredHolder<Block, T> registryObject) {
        super(registryObject);
    }
}
