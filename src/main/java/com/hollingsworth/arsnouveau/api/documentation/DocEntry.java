package com.hollingsworth.arsnouveau.api.documentation;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public record DocEntry(ResourceLocation id, List<SinglePageCtor> pages, ItemStack renderStack, Component component, int order) implements Comparable<DocEntry> {

    public DocEntry(ResourceLocation id, ItemStack renderStack, Component component) {
        this(id, new ArrayList<>(), renderStack, component, 1);
    }

    public DocEntry(ResourceLocation id, ItemStack renderStack, Component component, int order) {
        this(id, new ArrayList<>(), renderStack, component, order);
    }

    public void addPage(SinglePageCtor page){
        pages.add(page);
    }

    @Override
    public int compareTo(@NotNull DocEntry o) {
        return Integer.compare(order, o.order);
    }
}
