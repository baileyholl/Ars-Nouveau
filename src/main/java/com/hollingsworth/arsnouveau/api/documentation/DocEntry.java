package com.hollingsworth.arsnouveau.api.documentation;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public record DocEntry(ResourceLocation id, CopyOnWriteArrayList<SinglePageCtor> pages, ItemStack renderStack, Component component, int order) implements Comparable<DocEntry> {

    public DocEntry(ResourceLocation id, ItemStack renderStack, Component component) {
        this(id, new CopyOnWriteArrayList<>(), renderStack, component, 1);
    }

    public DocEntry(ResourceLocation id, ItemStack renderStack, Component component, int order) {
        this(id, new CopyOnWriteArrayList<>(), renderStack, component, order);
    }

    public void addPage(SinglePageCtor page){
        pages.add(page);
    }

    @Override
    public int compareTo(@NotNull DocEntry o) {
        return Integer.compare(order, o.order);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DocEntry docEntry = (DocEntry) o;
        return Objects.equals(id, docEntry.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
