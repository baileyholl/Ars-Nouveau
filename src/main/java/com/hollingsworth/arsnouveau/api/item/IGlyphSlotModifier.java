package com.hollingsworth.arsnouveau.api.item;

public interface IGlyphSlotModifier {
    default int getBonusGlyphSlots() {
        return 0;
    }
}
