package com.hollingsworth.arsnouveau.manifold.extensions.net.minecraft.nbt.CompoundTag;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;


@Extension
public class CompoundExtensions {
    public static BlockPos getBlockPos(@This CompoundTag thisTag, String key) {
        return new BlockPos(thisTag.getInt(key + "X"), thisTag.getInt(key + "Y"), thisTag.getInt(key + "Z"));
    }
}
