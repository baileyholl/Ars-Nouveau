package com.hollingsworth.arsnouveau.api.loot;

import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.*;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemFunction;
import net.minecraft.world.level.storage.loot.providers.number.BinomialDistributionGenerator;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import javax.annotation.Nullable;
import java.util.*;


public final class LootTableUtil {
    // Only works for alakarkinos and expected loot tables from archaeology, does not fetch from GLM or other special loot injection.
    public static Object2DoubleMap<Item> expectedDrops(MinecraftServer server, LootTable table) {
        Object2DoubleMap<Item> totals = new Object2DoubleOpenHashMap<>();
        Set<LootTable> visiting = Collections.newSetFromMap(new IdentityHashMap<>());
        calculateLootTable(server, table, 1.0, totals, visiting, List.of());
        return totals;
    }

    private static void calculateLootTable(MinecraftServer server, LootTable table, double multiplier,
                                           Object2DoubleMap<Item> totals, Set<LootTable> visiting,
                                           List<LootItemFunction> outer) {
        if (multiplier <= 0 || !visiting.add(table)) {
            return;
        }
        List<LootItemFunction> tableChain = chain(table.functions, outer);
        for (LootPool pool : table.pools) {
            calculateLootPool(server, pool, multiplier, totals, visiting, tableChain);
        }
        visiting.remove(table);
    }

    private static void calculateLootPool(MinecraftServer server, LootPool pool, double multiplier,
                                          Object2DoubleMap<Item> totals, Set<LootTable> visiting,
                                          List<LootItemFunction> outer) {
        double rolls = mean(pool.getRolls());
        if (rolls <= 0) {
            return;
        }
        List<FlatEntry> flat = new ArrayList<>();
        for (LootPoolEntryContainer container : pool.entries) {
            flatten(container, flat);
        }
        int totalWeight = 0;
        for (FlatEntry entry : flat) {
            totalWeight += entry.weight();
        }
        if (totalWeight <= 0) {
            return;
        }
        List<LootItemFunction> poolChain = chain(pool.functions, outer);
        for (FlatEntry entry : flat) {
            double share = multiplier * rolls * entry.weight() / totalWeight;
            accumulateEntry(server, entry, share, totals, visiting, poolChain);
        }
    }

    private static void accumulateEntry(MinecraftServer server, FlatEntry entry, double share,
                                        Object2DoubleMap<Item> totals, Set<LootTable> visiting,
                                        List<LootItemFunction> outer) {
        LootPoolSingletonContainer source = entry.source();
        List<LootItemFunction> entryChain = chain(source.functions, outer);

        if (entry.expandedTagItem() != null) {
            add(totals, resolveItem(entry.expandedTagItem(), outer), share * meanCount(outer));
            return;
        }
        switch (source) {
            case LootItem item ->
                    add(totals, resolveItem(item.item.value(), entryChain), share * meanCount(entryChain));
            case TagEntry tagEntry -> {
                double count = share * meanCount(entryChain);
                for (Holder<Item> holder : BuiltInRegistries.ITEM.getTagOrEmpty(tagEntry.tag)) {
                    add(totals, resolveItem(holder.value(), entryChain), count);
                }
            }
            case NestedLootTable nested -> {
                LootTable table = nested.contents.map(key -> server.reloadableRegistries().getLootTable(key), inline -> inline);
                if (table != LootTable.EMPTY) {
                    calculateLootTable(server, table, share, totals, visiting, entryChain);
                }
            }
            default -> {
            }
        }
    }

    private static void flatten(LootPoolEntryContainer container, List<FlatEntry> out) {
        switch (container) {
            case TagEntry tagEntry when tagEntry.expand -> {
                for (Holder<Item> holder : BuiltInRegistries.ITEM.getTagOrEmpty(tagEntry.tag)) {
                    out.add(new FlatEntry(tagEntry.weight, tagEntry, holder.value()));
                }
            }
            case LootPoolSingletonContainer singleton -> out.add(new FlatEntry(singleton.weight, singleton, null));
            case AlternativesEntry alternatives -> {
                if (!alternatives.children.isEmpty()) {
                    flatten(alternatives.children.getFirst(), out);
                }
            }
            case CompositeEntryBase composite -> {
                for (LootPoolEntryContainer child : composite.children) {
                    flatten(child, out);
                }
            }
            default -> {
            }
        }
    }

    private static Item resolveItem(Item base, List<LootItemFunction> chain) {
        Item current = base;
        for (LootItemFunction function : chain) {
            if (function instanceof SetItemFunction setItem) {
                current = setItem.item.value();
            }
        }
        return current;
    }

    private static double meanCount(List<LootItemFunction> chain) {
        double count = 1.0;
        for (LootItemFunction function : chain) {
            if (function instanceof SetItemCountFunction setCount) {
                count = setCount.add ? count + mean(setCount.value) : mean(setCount.value);
            }
        }
        return Math.max(count, 0);
    }

    private static double mean(NumberProvider provider) {
        return switch (provider) {
            case ConstantValue constant -> constant.value();
            case UniformGenerator uniform -> (mean(uniform.min()) + mean(uniform.max())) / 2.0;
            case BinomialDistributionGenerator binomial -> mean(binomial.n()) * mean(binomial.p());
            default -> 1.0;
        };
    }

    private static List<LootItemFunction> chain(List<LootItemFunction> inner, List<LootItemFunction> outer) {
        if (inner.isEmpty()) {
            return outer;
        }
        if (outer.isEmpty()) {
            return inner;
        }
        List<LootItemFunction> combined = new ArrayList<>(inner.size() + outer.size());
        combined.addAll(inner);
        combined.addAll(outer);
        return combined;
    }

    private static void add(Object2DoubleMap<Item> totals, Item item, double amount) {
        if (item != Items.AIR && amount > 0) {
            totals.put(item, totals.getDouble(item) + amount);
        }
    }

    private record FlatEntry(int weight, LootPoolSingletonContainer source, @Nullable Item expandedTagItem) {
    }
}
