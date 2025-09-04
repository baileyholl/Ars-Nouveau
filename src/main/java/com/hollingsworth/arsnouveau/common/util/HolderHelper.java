package com.hollingsworth.arsnouveau.common.util;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class HolderHelper {

    public static <T> Holder<T> unwrap(Level level, ResourceKey<T> key) {
        return level.registryAccess().registryOrThrow(key.registryKey()).getHolderOrThrow(key);
    }

    public static <T> Holder<T> unwrap(BlockEntity entity, ResourceKey<T> key) {
        return entity.getLevel().registryAccess().registryOrThrow(key.registryKey()).getHolderOrThrow(key);
    }
}
