package com.hollingsworth.arsnouveau.api.util;

import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class NBTUtil {

    public static CompoundTag storeBlockPos(CompoundTag tag, String prefix, BlockPos pos){
        if(pos == null)
            return tag;
        tag.putDouble(prefix + "_x", pos.getX());
        tag.putDouble(prefix + "_y", pos.getY());
        tag.putDouble(prefix + "_z", pos.getZ());
        return tag;
    }

    public static CompoundTag removeBlockPos(CompoundTag tag, String prefix){
        tag.remove(prefix + "_x");
        tag.remove(prefix + "_y");
        tag.remove(prefix + "_z");
        return tag;
    }

    public static BlockPos getBlockPos(CompoundTag tag, String prefix){
        return new BlockPos(tag.getDouble(prefix + "_x"), tag.getDouble(prefix + "_y"),tag.getDouble(prefix + "_z"));
    }

    public static boolean hasBlockPos(CompoundTag tag, String prefix){
        return tag.contains(prefix + "_x");
    }

    public static List<ItemStack> readItems(CompoundTag tag, String prefix){
        List<ItemStack> stacks = new ArrayList<>();
        if(tag == null)
            return stacks;

        for(String s : tag.getAllKeys()){
            if(s.contains(prefix)){
                stacks.add(ItemStack.of(tag.getCompound(s)));
            }
        }
        return stacks;
    }

    public static void writeItems(CompoundTag tag, String prefix, List<ItemStack> items){
        for(ItemStack item : items) {
            CompoundTag itemTag = new CompoundTag();
            item.save(itemTag);
            tag.put(getItemKey(item, prefix), itemTag);
        }
    }


    public static List<String> readStrings(CompoundTag tag, String prefix){
        List<String> strings = new ArrayList<>();
        if(tag == null)
            return strings;

        for(String s : tag.getAllKeys()){
            if(s.contains(prefix)){
                strings.add(tag.getString(s));
            }
        }
        return strings;
    }

    public static void writeStrings(CompoundTag tag, String prefix, Collection<String> strings){
        int i = 0;
        for(String s : strings){
            tag.putString(prefix + "_" + i, s);
            i++;
        }
    }

    public static String getItemKey(ItemStack stack, String prefix){
        return prefix + stack.getItem().getRegistryName().toString();
    }
}
