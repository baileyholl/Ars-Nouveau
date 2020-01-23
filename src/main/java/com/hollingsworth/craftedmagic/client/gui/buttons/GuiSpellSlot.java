package com.hollingsworth.craftedmagic.client.gui.buttons;

import com.hollingsworth.craftedmagic.ExampleMod;
import com.hollingsworth.craftedmagic.client.gui.GuiSpellCreation;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiSpellSlot extends GuiImageButton {


    public int slotNum;
    public boolean isSelected;

    public GuiSpellSlot(GuiSpellCreation parent, int x, int y, String resource_image, int slotNum) {
        super(x, y, 0, 0, 19, 19, "textures/gui/spell_cell.png", parent::onSlotChange);
        this.parent = parent;
        this.x = x;
        this.y = y;
        this.width = 19;
        this.height = 19;
        this.slotNum = slotNum;
        this.resourceIcon = resource_image;
        this.isSelected = false;
    }

    @Override
    public void render(int parX, int parY, float partialTicks) {
        if (visible)
        {
            ResourceLocation image;
            image = this.isSelected ? new ResourceLocation(ExampleMod.MODID, "textures/gui/spell_cell_selected.png") : new ResourceLocation(ExampleMod.MODID,"textures/gui/spell_cell.png");
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GuiSpellCreation.drawFromTexture(image, x, y, u, v, width, height);
            Minecraft.getInstance().fontRenderer.drawSplitString(String.valueOf(this.slotNum), x + 6, y + 5, 116, 0);
        }


    }
}
