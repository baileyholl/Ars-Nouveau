package com.hollingsworth.arsnouveau.common.util;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class RegistryWrapper<T> implements Supplier<T>, ItemLike {
    public RegistryObject<T> registryObject;

    public RegistryWrapper(RegistryObject<T> registryObject) {
        this.registryObject = registryObject;
    }

    @Nonnull
    @Override
    public T get() {
        return registryObject.get();
    }

    @Override
    public Item asItem() {
        if(registryObject.get() instanceof ItemLike itemLike){
            return itemLike.asItem();
        }
        throw new IllegalStateException("RegistryWrapper is not an Item");
    }

    public String getRegistryName() {
        return registryObject.getId().getPath();
    }
}
