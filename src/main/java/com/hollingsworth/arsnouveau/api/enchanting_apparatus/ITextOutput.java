package com.hollingsworth.arsnouveau.api.enchanting_apparatus;

import net.minecraft.network.chat.Component;

public interface ITextOutput {

    /**
     * Returns the component that should be displayed in the output slot.
     */
    Component getOutputComponent();
}
