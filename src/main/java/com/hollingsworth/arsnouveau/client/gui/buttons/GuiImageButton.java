package com.hollingsworth.arsnouveau.client.gui.buttons;


import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.gui.book.GuiSpellBook;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class GuiImageButton extends Button
{
    private ResourceLocation image;

    public String resourceIcon;

    int u, v, image_width, image_height;
    GuiSpellBook parent;



    public GuiImageButton( int x, int y,int u,int v,int w, int h, int image_width, int image_height, String resource_image, Button.IPressable onPress) {
        super(x, y, w, h, new StringTextComponent(""), onPress);
        this.x = x;
        this.y = y;
        this.resourceIcon = resource_image;

        this.u = u;
        this.v = v;
        this.image_height = image_height;
        this.image_width = image_width;
        //System.out.println(width);
        image = new ResourceLocation(ArsNouveau.MODID, resource_image);

    }

    @Override
    protected void renderBg(MatrixStack p_230441_1_, Minecraft p_230441_2_, int p_230441_3_, int p_230441_4_) {

    }

    @Override
    public void render(MatrixStack ms, int parX, int parY, float partialTicks) {
//        super.render(ms, parX, parY, partialTicks);
        if (visible)
        {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GuiSpellBook.drawFromTexture(image, x, y, u, v, width, height, image_width, image_height,ms);
        }
    }
}