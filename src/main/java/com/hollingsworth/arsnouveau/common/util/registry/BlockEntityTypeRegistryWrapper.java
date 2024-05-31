package com.hollingsworth.arsnouveau.common.util.registry;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;

public class BlockEntityTypeRegistryWrapper<T extends BlockEntity> extends RegistryWrapper<BlockEntityType<?>, BlockEntityType<T>> {
    public BlockEntityTypeRegistryWrapper(DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> registryObject) {
        super(registryObject);
    }
}
