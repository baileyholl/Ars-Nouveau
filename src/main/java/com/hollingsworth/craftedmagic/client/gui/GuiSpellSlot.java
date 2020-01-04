package com.hollingsworth.craftedmagic.client.gui;

import com.hollingsworth.craftedmagic.ExampleMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
class GuiSpellSlot extends GuiImageButton
{

    public boolean isCraftingSlot;
    public String resourceIcon;
    public String spell_id; //Reference to a spell ID for spell crafting

    public GuiSpellSlot(int parButtonId, int parPosX, int parPosY,boolean isCraftingSlot, String resource_image,  String spell_id) {
        super(parButtonId, parPosX, parPosY, 0, 0, 18, 18, "textures/gui/spell_cell.png");
        this.isCraftingSlot = isCraftingSlot;
        this.resourceIcon = resource_image;
        this.spell_id = spell_id;
    }


    public GuiSpellSlot(int parButtonId, int parPosX, int parPosY,boolean isCraftingSlot, String resource_image) {
        this(parButtonId, parPosX, parPosY, isCraftingSlot, resource_image, "");

    }
    /**
     * Draws this button to the screen.
     */
    @Override
    public void drawButton(Minecraft mc, int parX, int parY, float partialTicks)
    {
        super.drawButton(mc, parX, parY, partialTicks);
        if (visible)
        {
            if(this.resourceIcon != null && !this.resourceIcon.equals("")) {
                mc.getTextureManager().bindTexture(new ResourceLocation(ExampleMod.MODID, "textures/gui/spells/" + this.resourceIcon));
                drawTexturedModalRect(x + 2, y + 2, 0, 0, 16, 16);
            }

        }
    }
}