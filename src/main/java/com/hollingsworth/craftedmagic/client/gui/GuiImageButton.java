package com.hollingsworth.craftedmagic.client.gui;


import com.hollingsworth.craftedmagic.ExampleMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
class GuiImageButton extends GuiButton
{
    private ResourceLocation image;

    public String resourceIcon;

    int texture_x, texture_y, texture_height, texture_width;
    public GuiImageButton(int parButtonId, int parPosX, int parPosY,int texture_x,int texture_y,int texture_height, int texture_width, String resource_image) {
        super(parButtonId, parPosX, parPosY, parPosX, parPosY, "");
        this.resourceIcon = resource_image;
        this.texture_x = texture_x;
        this.texture_y = texture_y;
        this.texture_height = texture_height;
        this.texture_width = texture_width;
        this.width = texture_width;
        this.height = texture_height;
        image = new ResourceLocation(ExampleMod.MODID, resource_image);

    }

    /**
     * Draws this button to the screen.
     */
    @Override
    public void drawButton(Minecraft mc, int parX, int parY, float partialTicks)
    {
        if (visible)
        {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            mc.getTextureManager().bindTexture(image);
            drawTexturedModalRect(x, y,
                    this.texture_x, this.texture_y,
                    this.texture_width, this.texture_height);
        }
    }
}