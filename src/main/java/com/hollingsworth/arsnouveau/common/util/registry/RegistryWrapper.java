package com.hollingsworth.arsnouveau.common.util.registry;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class RegistryWrapper<R, T extends R> implements Supplier<T>, ItemLike {
    public DeferredHolder<R, T> registryObject;

    public RegistryWrapper(DeferredHolder<R, T> registryObject) {
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

    public Holder<R> getHolder() {
        return registryObject.getDelegate();
    }

    public ResourceLocation getResourceLocation() {
        return registryObject.getId();
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
