package com.hollingsworth.arsnouveau.items;

import net.minecraft.item.Item;

public class ModItem extends Item {
    public ModItem(Properties properties) {
        super(properties);
    }

    public ModItem(Properties properties, String registryName){
        this(properties);
        setRegistryName(registryName);
    }

    public ModItem(String registryName){
        this(ItemsRegistry.defaultItemProperties(), registryName);
    }
}
