package com.hollingsworth.arsnouveau.common.crafting.recipes;

import net.minecraft.network.chat.Component;

public interface ITextOutput {

    /**
     * Returns the component that should be displayed in the output slot.
     */
    Component getOutputComponent();
}
