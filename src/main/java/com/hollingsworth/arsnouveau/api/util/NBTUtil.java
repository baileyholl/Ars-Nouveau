package com.hollingsworth.arsnouveau.api.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.hollingsworth.arsnouveau.setup.registry.RegistryHelper.getRegistryName;

public class NBTUtil {

    public static CompoundTag storeBlockPos(CompoundTag tag, String prefix, BlockPos pos) {
        if (pos == null)
            return tag;
        writePositional(tag, prefix, pos.getX(), pos.getY(), pos.getZ());
        return tag;
    }

    public static CompoundTag storeVec(CompoundTag tag, String prefix, Vec3 vec){
        writePositional(tag, prefix, vec.x, vec.y, vec.z);
        return tag;
    }

    public static CompoundTag removeBlockPos(CompoundTag tag, String prefix) {
        tag.remove(prefix + "_x");
        tag.remove(prefix + "_y");
        tag.remove(prefix + "_z");
        return tag;
    }

    public static CompoundTag writePositional(CompoundTag tag, String prefix, double x, double y, double z){
        tag.putDouble(prefix + "_x", x);
        tag.putDouble(prefix + "_y", y);
        tag.putDouble(prefix + "_z", z);
        return tag;
    }

    @Deprecated
    public static BlockPos getBlockPos(CompoundTag tag, String prefix) {
        return BlockPos.containing(tag.getDouble(prefix + "_x"), tag.getDouble(prefix + "_y"), tag.getDouble(prefix + "_z"));
    }

    public static Vec3 getVec(CompoundTag tag, String prefix){
        if(tag == null){
            return null;
        }
        return new Vec3(tag.getDouble(prefix + "_x"), tag.getDouble(prefix + "_y"), tag.getDouble(prefix + "_z"));
    }

    public static @Nullable BlockPos getNullablePos(CompoundTag tag, String prefix) {
        if(!tag.contains(prefix + "_x"))
            return null;
        return BlockPos.containing(tag.getDouble(prefix + "_x"), tag.getDouble(prefix + "_y"), tag.getDouble(prefix + "_z"));
    }


    public static boolean hasBlockPos(CompoundTag tag, String prefix) {
        return tag.contains(prefix + "_x");
    }

    public static List<ItemStack> readItems(HolderLookup.Provider pRegistries, CompoundTag tag, String prefix) {
        List<ItemStack> stacks = new ArrayList<>();

        if (tag == null)
            return stacks;
        try {
            CompoundTag itemsTag = tag.getCompound(prefix + "_tag");
            int numItems = itemsTag.getInt("itemsSize");
            for (int i = 0; i < numItems; i++) {
                String key = prefix + "_" + i;
                stacks.add(ItemStack.parseOptional(pRegistries, itemsTag.getCompound(key)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stacks;
    }

    public static void writeItems(HolderLookup.Provider pRegistries, CompoundTag tag, String prefix, List<ItemStack> items) {
        CompoundTag allItemsTag = new CompoundTag();
        for (int i = 0; i < items.size(); i++) {
            ItemStack stack = items.get(i);
            Tag itemTag = stack.save(pRegistries);
            allItemsTag.put(prefix + "_" + i, itemTag);
        }
        allItemsTag.putInt("itemsSize", items.size());
        tag.put(prefix + "_tag", allItemsTag);
    }


    public static List<String> readStrings(CompoundTag tag, String prefix) {
        List<String> strings = new ArrayList<>();
        if (tag == null)
            return strings;

        for (String s : tag.getAllKeys()) {
            if (s.contains(prefix)) {
                strings.add(tag.getString(s));
            }
        }
        return strings;
    }

    public static void writeStrings(CompoundTag tag, String prefix, Collection<String> strings) {
        int i = 0;
        for (String s : strings) {
            tag.putString(prefix + "_" + i, s);
            i++;
        }
    }

    public static void writeResourceLocations(CompoundTag tag, String prefix, Collection<ResourceLocation> resourceLocations) {
        writeStrings(tag, prefix, resourceLocations.stream().map(ResourceLocation::toString).collect(Collectors.toList()));
    }

    public static List<ResourceLocation> readResourceLocations(CompoundTag tag, String prefix) {
        return readStrings(tag, prefix).stream().map(ResourceLocation::tryParse).collect(Collectors.toList());
    }

    public static String getItemKey(ItemStack stack, String prefix) {
        return prefix + getRegistryName(stack.getItem()).toString();
    }
}
