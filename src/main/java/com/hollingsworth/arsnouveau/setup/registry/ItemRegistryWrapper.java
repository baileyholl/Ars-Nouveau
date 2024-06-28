package com.hollingsworth.arsnouveau.setup.registry;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ItemRegistryWrapper<T extends Item> extends RegistryWrapper<Item, T> {
    public ItemRegistryWrapper(DeferredHolder<Item, T> registryObject) {
        super(registryObject);
    }
}
