package com.hollingsworth.arsnouveau.common.util;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

// yoinked from Ars Instrumentum
public class SerializationUtil {
    public static final int COMPOUND_TAG_TYPE = 10;
    private static final String X = "x";
    private static final String Y = "y";
    private static final String Z = "z";

    public static ListTag serializeItemList(List<ItemStack> items) {
        ListTag itemList = new ListTag();
        items.forEach((itemstack) -> itemList.add(itemstack.serializeNBT()));
        return itemList;
    }

    public static List<ItemStack> deserializeItemList(CompoundTag compoundTag, String tag) {
        List<ItemStack> itemStacks = new ArrayList<>();
        if (compoundTag.contains(tag)) {
            ListTag itemList = compoundTag.getList(tag, COMPOUND_TAG_TYPE);
            for (int i = 0; i < itemList.size(); i++) {
                itemStacks.add(ItemStack.of(itemList.getCompound(i)));
            }
            return itemStacks;
        }
        return itemStacks;
    }

    public static ListTag serializeTagList(List<CompoundTag> tags){
        ListTag tagList = new ListTag();
        tagList.addAll(tags);
        return tagList;
    }

    public static <T> List<T> mapFromTags(ListTag tagList, Function<CompoundTag, T> transformer){
        List<T> list = new ArrayList<>();
        for(int i = 0; i < tagList.size(); i++){
            list.add(transformer.apply(tagList.getCompound(i)));
        }
        return list;
    }

    public static ListTag serializeBlockPosList(List<BlockPos> blockPositions) {
        ListTag serializedBlockPositions = new ListTag();
        blockPositions.forEach(blockPos -> serializedBlockPositions.add(serializeBlockPos(blockPos)));
        return serializedBlockPositions;
    }

    public static CompoundTag serializeBlockPos(BlockPos blockPos) {
        CompoundTag serializedBlockPos = new CompoundTag();
        serializedBlockPos.putInt(X, blockPos.getX());
        serializedBlockPos.putInt(Y, blockPos.getY());
        serializedBlockPos.putInt(Z, blockPos.getZ());
        return serializedBlockPos;
    }

    public static List<BlockPos> deserializeBlockPosList(CompoundTag tag, String key) {
        ListTag serializedBlockPositions = tag.getList(key, COMPOUND_TAG_TYPE);
        List<BlockPos> blockPositions = new ArrayList<>();
        for (int i = 0; i < serializedBlockPositions.size(); i++) {
            blockPositions.add(deserializeBlockPos(serializedBlockPositions.getCompound(i)));
        }
        return blockPositions;
    }

    public static BlockPos deserializeBlockPos(CompoundTag serializedBlockPosition) {
        return new BlockPos(serializedBlockPosition.getInt(X), serializedBlockPosition.getInt(Y), serializedBlockPosition.getInt(Z));
    }
}
