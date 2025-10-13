package com.hollingsworth.arsnouveau.common.util;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PaintingUtil {
    public static Set<ItemStack> getAllPaintings(HolderLookup.Provider registries) {
        return getAllPaintings(registries, ArsNouveau.MODID);
    }

    public static Set<ItemStack> getAllPaintings(HolderLookup.Provider registries, String id) {
        return registries.lookup(Registries.PAINTING_VARIANT)
            .map(lookup -> {
                RegistryOps<Tag> registryOps = registries.createSerializationContext(NbtOps.INSTANCE);
                return lookup.listElementIds()
                    .filter(key -> key.location().getNamespace().equals(id))
                    .map(key -> getPainting(registries, key, registryOps))
                    .collect(Collectors.toSet());
            }).orElse(new HashSet<>());
    }

    public static ItemStack getPainting(HolderLookup.Provider registries, ResourceKey<PaintingVariant> key) {
        RegistryOps<Tag> registryOps = registries.createSerializationContext(NbtOps.INSTANCE);

        return getPainting(registries, key, registryOps);
    }

    public static ItemStack getPainting(HolderLookup.Provider registries, ResourceKey<PaintingVariant> key, RegistryOps<Tag> registryOps) {
        Holder.Reference<PaintingVariant> painting = registries.lookup(Registries.PAINTING_VARIANT)
                .flatMap(lookup -> lookup.get(key))
                .orElse(null);
        if (painting == null) return ItemStack.EMPTY;

        ItemStack is = new ItemStack(Items.PAINTING);
        CustomData data = CustomData.EMPTY
                .update(tag -> tag.putString("id", "minecraft:painting"))
                .update(registryOps, Painting.VARIANT_MAP_CODEC, painting)
                .mapOrElse(Function.identity(),err -> CustomData.EMPTY);
        is.set(DataComponents.ENTITY_DATA, data);
        return is;
    }
}
