package com.hollingsworth.arsnouveau.client.gui.buttons;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.spell.ISpellCaster;
import com.hollingsworth.arsnouveau.api.util.CasterUtil;
import com.hollingsworth.arsnouveau.client.gui.book.GuiSpellBook;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * Slots for selecting the spell recipes stored in the book.
 */
public class GuiSpellSlot extends GuiImageButton {


    public int slotNum;
    public boolean isSelected;

    public GuiSpellSlot(GuiSpellBook parent, int x, int y, int slotNum) {
        super(x, y, 0, 0, 18, 13, 18, 13, "textures/gui/spell_tab.png", parent::onSlotChange);
        this.parent = parent;
        this.slotNum = slotNum;
        this.isSelected = false;
    }

    @Override
    public void render(GuiGraphics graphics, int parX, int parY, float partialTicks) {
        if (visible) {
            if (parent.isMouseInRelativeRange(parX, parY, x, y, width, height)) {
                ISpellCaster caster = CasterUtil.getCaster(((GuiSpellBook) parent).bookStack);
                String name = caster.getSpellName(slotNum);
                if (!name.isEmpty()) {
                    List<Component> tip = new ArrayList<>();
                    tip.add(Component.literal(name));
                    parent.tooltip = tip;
                }
            }

            ResourceLocation image;
            image = this.isSelected ? new ResourceLocation(ArsNouveau.MODID, "textures/gui/spell_tab_selected.png") : new ResourceLocation(ArsNouveau.MODID, "textures/gui/spell_tab.png");
            //GuiSpellBook.drawFromTexture(image, x, y, u, v, width, height, width, height);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            GuiSpellBook.drawFromTexture(image, x, y, u, v, width, height, image_width, image_height, graphics);
            graphics.drawString(Minecraft.getInstance().font, String.valueOf(this.slotNum + 1), x + 8, y + 3, 16777215); // White
        }
    }
}
