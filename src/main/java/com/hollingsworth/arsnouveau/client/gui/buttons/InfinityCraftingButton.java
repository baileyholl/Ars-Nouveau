package com.hollingsworth.arsnouveau.client.gui.buttons;

import net.minecraft.client.gui.components.Button;
import net.minecraft.resources.ResourceLocation;

public class InfinityCraftingButton extends CraftingButton {
    public int slotNum;
    public ResourceLocation spellTag;

    public InfinityCraftingButton(int x, int y, Button.OnPress onPress) {
        super(x, y, onPress);
    }

}