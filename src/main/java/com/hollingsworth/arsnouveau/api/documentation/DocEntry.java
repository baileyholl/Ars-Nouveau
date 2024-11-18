package com.hollingsworth.arsnouveau.api.documentation;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public record DocEntry(ResourceLocation id, List<SinglePageCtor> pages, ItemStack renderStack, Component component) {

    public DocEntry(ResourceLocation id, ItemStack renderStack, Component component) {
        this(id, new ArrayList<>(), renderStack, component);
    }

    public void addPage(SinglePageCtor page){
        pages.add(page);
    }
}
