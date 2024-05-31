package com.hollingsworth.arsnouveau.common.util.registry;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ItemRegistryWrapper<T extends Item> extends RegistryWrapper<Item, T> {
    public ItemRegistryWrapper(DeferredHolder<Item, T> registryObject) {
        super(registryObject);
    }
}
