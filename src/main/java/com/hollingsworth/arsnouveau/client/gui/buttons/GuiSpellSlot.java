package com.hollingsworth.arsnouveau.client.gui.buttons;

import com.hollingsworth.arsnouveau.api.documentation.DocAssets;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.List;

/**
 * Slots for selecting the spell recipes stored in the book.
 */
public class GuiSpellSlot extends SelectableButton {

    public int slotNum;
    public String spellName;

    public GuiSpellSlot(int x, int y, int slotNum, String spellName, OnPress onPress) {
        super(x, y, DocAssets.SPELL_TAB_ICON, DocAssets.SPELL_TAB_ICON_SELECTED, onPress);
        this.slotNum = slotNum;
        this.spellName = spellName;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.renderWidget(graphics, pMouseX, pMouseY, pPartialTick);
        graphics.drawCenteredString(Minecraft.getInstance().font, String.valueOf(this.slotNum + 1), x + 8, y + 3, 16777215); // White
    }

    @Override
    public void getTooltip(List<Component> tooltip) {
        if (!spellName.isEmpty()) {
            tooltip.add(Component.literal(spellName));
        }
    }
}
