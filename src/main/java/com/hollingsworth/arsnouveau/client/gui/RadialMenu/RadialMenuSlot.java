package com.hollingsworth.arsnouveau.client.gui.RadialMenu;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.List;

public record RadialMenuSlot(String slotName, Item primarySlotIcon, List<Item> secondarySlotIcons) {
}
