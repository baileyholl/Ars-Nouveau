package com.hollingsworth.craftedmagic.client.gui.buttons;

import com.hollingsworth.craftedmagic.ArsNouveau;
import com.hollingsworth.craftedmagic.client.gui.GuiSpellBook;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * Slots for selecting the spell recipes stored in the book.
 */
public class GuiSpellSlot extends GuiImageButton {


    public int slotNum;
    public boolean isSelected;

    public GuiSpellSlot(GuiSpellBook parent, int x, int y,  int slotNum) {
        super(x, y, 0, 0, 20, 12, 20, 12,"textures/gui/tab.png", parent::onSlotChange);
        this.parent = parent;
        this.slotNum = slotNum;
        this.isSelected = false;
    }

    @Override
    public void render(int parX, int parY, float partialTicks) {
        if (visible)
        {
            ResourceLocation image;
            image = this.isSelected ? new ResourceLocation(ArsNouveau.MODID, "textures/gui/tab_selected.png") : new ResourceLocation(ArsNouveau.MODID,"textures/gui/tab.png");
            //GuiSpellBook.drawFromTexture(image, x, y, u, v, width, height, width, height);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GuiSpellBook.drawFromTexture(image, x, y, u, v, width, height, image_width, image_height);
            Minecraft.getInstance().fontRenderer.drawSplitString(String.valueOf(this.slotNum), x + 6, y + 2, 116, 16777215); // White
        }
    }
}
