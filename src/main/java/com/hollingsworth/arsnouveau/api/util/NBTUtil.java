package com.hollingsworth.arsnouveau.api.util;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;

public class NBTUtil {

    public static CompoundNBT storeBlockPos(CompoundNBT tag, String prefix, BlockPos pos){
        tag.putDouble(prefix + "_x", pos.getX());
        tag.putDouble(prefix + "_y", pos.getY());
        tag.putDouble(prefix + "_z", pos.getZ());
        return tag;
    }

    public static BlockPos getBlockPos(CompoundNBT tag, String prefix){
        return new BlockPos(tag.getDouble(prefix + "_x"), tag.getDouble(prefix + "_y"),tag.getDouble(prefix + "_z"));
    }

    public static boolean hasBlockPos(CompoundNBT tag, String prefix){
        return tag.contains(prefix + "_x");
    }
}
