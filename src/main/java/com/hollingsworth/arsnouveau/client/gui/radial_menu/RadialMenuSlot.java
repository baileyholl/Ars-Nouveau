package com.hollingsworth.arsnouveau.client.gui.radial_menu;

import java.util.List;

public record RadialMenuSlot<T>(String slotName, T primarySlotIcon, List<T> secondarySlotIcons) {
}
