package com.hollingsworth.arsnouveau.api.registry;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.perk.IPerk;
import com.hollingsworth.arsnouveau.api.perk.PerkSlot;
import com.hollingsworth.arsnouveau.common.items.PerkItem;
import com.mojang.serialization.Lifecycle;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class PerkRegistry {
    public static final Registry<IPerk> PERK_TYPES = new MappedRegistry<>(ResourceKey.createRegistryKey(ArsNouveau.prefix("perk_types")), Lifecycle.stable());
    public static final Registry<PerkItem> PERK_ITEMS = new MappedRegistry<>(ResourceKey.createRegistryKey(ArsNouveau.prefix("perk_items")), Lifecycle.stable());

    private static final ConcurrentHashMap<Item, List<List<PerkSlot>>> itemPerkProviderMap = new ConcurrentHashMap<>();

    public static void registerPerk(IPerk perk) {
        Registry.registerForHolder(PERK_TYPES, perk.getRegistryName(), perk);
    }

    public static boolean registerPerkProvider(ItemLike item, List<List<PerkSlot>> tierList) {
        return itemPerkProviderMap.put(item.asItem(), tierList) == null;
    }

    public static void registerPerkItem(PerkItem perkItem) throws IllegalStateException {
        if (!PERK_TYPES.containsValue(perkItem.perk)) {
            throw new IllegalStateException("Perk '" + perkItem.perk.getRegistryName() + "' for '" + perkItem.asItem().getDescriptionId() + "' is not registered");
        }

        Registry.registerForHolder(PERK_ITEMS, perkItem.perk.getRegistryName(), perkItem);
    }

    public static @Nullable List<List<PerkSlot>> getPerkProvider(Item item) {
        return itemPerkProviderMap.get(item);
    }

    public static @Nullable List<List<PerkSlot>> getPerkProvider(ItemStack item) {
        return getPerkProvider(item.getItem());
    }

    public static @NotNull List<Item> getPerkProviderItems() {
        List<Item> list = new ArrayList<>();
        for (Iterator<Item> it = itemPerkProviderMap.keys().asIterator(); it.hasNext(); ) {
            Item i = it.next();
            list.add(i);
        }
        return list;
    }
}
