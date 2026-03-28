package com.hollingsworth.arsnouveau.api.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.hollingsworth.arsnouveau.setup.registry.RegistryHelper.getRegistryName;

public class NBTUtil {

    public static int INT_LIST_TAG_TYPE = 11;

    public static CompoundTag storeBlockPos(CompoundTag tag, String prefix, BlockPos pos) {
        if (pos == null)
            return tag;
        writePositional(tag, prefix, pos.getX(), pos.getY(), pos.getZ());
        return tag;
    }

    public static CompoundTag storeVec(CompoundTag tag, String prefix, Vec3 vec) {
        writePositional(tag, prefix, vec.x, vec.y, vec.z);
        return tag;
    }

    public static CompoundTag removeBlockPos(CompoundTag tag, String prefix) {
        tag.remove(prefix + "_x");
        tag.remove(prefix + "_y");
        tag.remove(prefix + "_z");
        return tag;
    }

    public static CompoundTag writePositional(CompoundTag tag, String prefix, double x, double y, double z) {
        tag.putDouble(prefix + "_x", x);
        tag.putDouble(prefix + "_y", y);
        tag.putDouble(prefix + "_z", z);
        return tag;
    }

    @Deprecated
    public static BlockPos getBlockPos(CompoundTag tag, String prefix) {
        // 1.21.11: CompoundTag.getDouble() returns Optional<Double>; use getDoubleOr()
        return BlockPos.containing(tag.getDoubleOr(prefix + "_x", 0), tag.getDoubleOr(prefix + "_y", 0), tag.getDoubleOr(prefix + "_z", 0));
    }

    public static BlockPos getPos(int[] arr) {
        return new BlockPos(arr[0], arr[1], arr[2]);
    }

    public static Vec3 getVec(CompoundTag tag, String prefix) {
        if (tag == null) {
            return null;
        }
        return new Vec3(tag.getDoubleOr(prefix + "_x", 0), tag.getDoubleOr(prefix + "_y", 0), tag.getDoubleOr(prefix + "_z", 0));
    }

    public static @Nullable BlockPos getNullablePos(CompoundTag tag, String prefix) {
        if (!tag.contains(prefix + "_x"))
            return null;
        return BlockPos.containing(tag.getDoubleOr(prefix + "_x", 0), tag.getDoubleOr(prefix + "_y", 0), tag.getDoubleOr(prefix + "_z", 0));
    }


    public static boolean hasBlockPos(CompoundTag tag, String prefix) {
        return tag.contains(prefix + "_x");
    }

    // --- ValueInput / ValueOutput overloads (BlockEntity serialization in MC 1.21.11+) ---

    public static void storeBlockPos(ValueOutput tag, String prefix, BlockPos pos) {
        if (pos == null) return;
        tag.putDouble(prefix + "_x", pos.getX());
        tag.putDouble(prefix + "_y", pos.getY());
        tag.putDouble(prefix + "_z", pos.getZ());
    }

    public static boolean hasBlockPos(ValueInput tag, String prefix) {
        return !Double.isNaN(tag.getDoubleOr(prefix + "_x", Double.NaN));
    }

    public static @Nullable BlockPos getNullablePos(ValueInput tag, String prefix) {
        if (!hasBlockPos(tag, prefix)) return null;
        return getBlockPos(tag, prefix);
    }

    public static BlockPos getBlockPos(ValueInput tag, String prefix) {
        return BlockPos.containing(
                tag.getDoubleOr(prefix + "_x", 0),
                tag.getDoubleOr(prefix + "_y", 0),
                tag.getDoubleOr(prefix + "_z", 0));
    }

    public static void removeBlockPos(ValueOutput tag, String prefix) {
        tag.discard(prefix + "_x");
        tag.discard(prefix + "_y");
        tag.discard(prefix + "_z");
    }

    public static List<ItemStack> readItems(ValueInput tag, String prefix) {
        List<ItemStack> stacks = new ArrayList<>();
        tag.listOrEmpty(prefix + "_tag", ItemStack.OPTIONAL_CODEC).forEach(stacks::add);
        return stacks;
    }

    public static void writeItems(ValueOutput tag, String prefix, List<ItemStack> items) {
        ValueOutput.TypedOutputList<ItemStack> list = tag.list(prefix + "_tag", ItemStack.OPTIONAL_CODEC);
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) {
                list.add(stack);
            }
        }
    }

    // --- Legacy CompoundTag overloads (entity serialization still uses CompoundTag) ---

    public static List<ItemStack> readItems(HolderLookup.Provider pRegistries, CompoundTag tag, String prefix) {
        List<ItemStack> stacks = new ArrayList<>();

        if (tag == null)
            return stacks;
        try {
            // 1.21.11: getCompound() returns Optional<CompoundTag>; use getCompoundOrEmpty()
            // ItemStack.parseOptional() removed; use OPTIONAL_CODEC with RegistryOps
            CompoundTag itemsTag = tag.getCompoundOrEmpty(prefix + "_tag");
            int numItems = itemsTag.getIntOr("itemsSize", 0);
            var registryOps = RegistryOps.create(NbtOps.INSTANCE, pRegistries);
            for (int i = 0; i < numItems; i++) {
                String key = prefix + "_" + i;
                CompoundTag itemTag = itemsTag.getCompoundOrEmpty(key);
                ItemStack.OPTIONAL_CODEC.parse(registryOps, itemTag).resultOrPartial(e -> {}).ifPresent(stacks::add);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stacks;
    }

    public static void writeItems(HolderLookup.Provider pRegistries, CompoundTag tag, String prefix, List<ItemStack> items) {
        // 1.21.11: stack.save(registries) removed; use OPTIONAL_CODEC with RegistryOps
        CompoundTag allItemsTag = new CompoundTag();
        items = items.stream().filter(stack -> !stack.isEmpty()).collect(Collectors.toList());
        var registryOps = RegistryOps.create(NbtOps.INSTANCE, pRegistries);
        for (int i = 0; i < items.size(); i++) {
            ItemStack stack = items.get(i);
            final int idx = i;
            ItemStack.OPTIONAL_CODEC.encodeStart(registryOps, stack).resultOrPartial(e -> {})
                    .ifPresent(itemTag -> allItemsTag.put(prefix + "_" + idx, itemTag));
        }
        allItemsTag.putInt("itemsSize", items.size());
        tag.put(prefix + "_tag", allItemsTag);
    }


    public static List<String> readStrings(CompoundTag tag, String prefix) {
        List<String> strings = new ArrayList<>();
        if (tag == null)
            return strings;

        // 1.21.11: getAllKeys() → keySet(); getString() returns Optional<String> → use getStringOr()
        for (String s : tag.keySet()) {
            if (s.contains(prefix)) {
                strings.add(tag.getStringOr(s, ""));
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

    public static void writeResourceLocations(CompoundTag tag, String prefix, Collection<Identifier> resourceLocations) {
        writeStrings(tag, prefix, resourceLocations.stream().map(Identifier::toString).collect(Collectors.toList()));
    }

    public static List<Identifier> readResourceLocations(CompoundTag tag, String prefix) {
        return readStrings(tag, prefix).stream().map(Identifier::tryParse).collect(Collectors.toList());
    }

    public static String getItemKey(ItemStack stack, String prefix) {
        return prefix + getRegistryName(stack.getItem()).toString();
    }
}
