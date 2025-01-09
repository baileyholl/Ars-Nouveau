package com.hollingsworth.arsnouveau.api.documentation.search;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public record ConnectedSearch(ResourceLocation entryId, Component title, ItemStack icon) {
}
