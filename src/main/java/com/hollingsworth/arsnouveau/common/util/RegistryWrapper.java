package com.hollingsworth.arsnouveau.common.util;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class RegistryWrapper<T> implements Supplier<T>, ItemLike {
    public RegistryObject<T> registryObject;

    public RegistryWrapper(RegistryObject<T> registryObject) {
        this.registryObject = registryObject;
    }

   @NotNull
    @Override
    public T get() {
        return registryObject.get();
    }

    @Override
    public Item asItem() {
        if (registryObject.get() instanceof ItemLike itemLike) {
            return itemLike.asItem();
        }
        throw new IllegalStateException("RegistryWrapper is not an Item");
    }

    public String getRegistryName() {
        return registryObject.getId().getPath();
    }

    public BlockState defaultBlockState(){
        if(registryObject.get() instanceof Block block){
            return block.defaultBlockState();
        }
        throw new IllegalStateException("RegistryWrapper is not a Block");
    }
}
