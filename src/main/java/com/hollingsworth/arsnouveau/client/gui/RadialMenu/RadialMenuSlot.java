package com.hollingsworth.arsnouveau.client.gui.RadialMenu;

import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record RadialMenuSlot(String slotName, ResourceLocation primarySlotIcon, List<ResourceLocation> secondarySlotIcons) {
}
