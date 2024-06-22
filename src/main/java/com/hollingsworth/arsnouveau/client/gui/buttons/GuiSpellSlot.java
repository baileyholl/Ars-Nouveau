package com.hollingsworth.arsnouveau.client.gui.buttons;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

/**
 * Slots for selecting the spell recipes stored in the book.
 */
public class GuiSpellSlot extends GuiImageButton {


    public int slotNum;
    public boolean isSelected;
    public String spellName;
    public GuiSpellSlot(int x, int y, int slotNum, String spellName, OnPress onPress) {
        super(x, y, 0, 0, 18, 13, 18, 13, "textures/gui/spell_tab.png", onPress);
        this.slotNum = slotNum;
        this.isSelected = false;
        this.spellName = spellName;
    }

    @Override
    public void render(GuiGraphics graphics, int parX, int parY, float partialTicks) {
        if (visible) {
            image = this.isSelected ? ArsNouveau.prefix( "textures/gui/spell_tab_selected.png") : ArsNouveau.prefix( "textures/gui/spell_tab.png");
            super.render(graphics, parX, parY, partialTicks);
            graphics.drawCenteredString(Minecraft.getInstance().font, String.valueOf(this.slotNum + 1), x + 8, y + 3, 16777215); // White
        }
    }

    @Override
    public void getTooltip(List<Component> tooltip) {
        if (!spellName.isEmpty()) {
            tooltip.add(Component.literal(spellName));
        }
    }
}
