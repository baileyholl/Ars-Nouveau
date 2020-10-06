package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ModItem extends Item {
    public ModItem(Properties properties) {
        super(properties);
    }

    public ModItem(Properties properties, String registryName){
        this(properties);
        setRegistryName(ArsNouveau.MODID, registryName);
    }

    public ModItem(String registryName){
        this(ItemsRegistry.defaultItemProperties(), registryName);
    }

    public ItemStack getStack(){
        return new ItemStack(this);
    }
}
