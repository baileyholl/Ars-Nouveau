package com.hollingsworth.craftedmagic.client.gui.buttons;


import com.hollingsworth.craftedmagic.ExampleMod;
import com.hollingsworth.craftedmagic.client.gui.GuiSpellCreation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

@OnlyIn(Dist.CLIENT)
public class GuiImageButton extends Button
{
    private ResourceLocation image;

    public String resourceIcon;

    int u, v;
    GuiSpellCreation parent;



    public GuiImageButton( int x, int y,int u,int v,int w, int h, String resource_image, Button.IPressable onPress) {
        super(x, y, w, h, "", onPress);

        this.resourceIcon = resource_image;
        this.u = u;
        this.v = v;

        //System.out.println(width);
        image = new ResourceLocation(ExampleMod.MODID, resource_image);

    }

    /**
     * Draws this button to the screen.
     */
    @Override
    public void render(int parX, int parY, float partialTicks)
    {
        if (visible)
        {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GuiSpellCreation.drawFromTexture(image, x, y, u, v, width, height);
        }
    }
}