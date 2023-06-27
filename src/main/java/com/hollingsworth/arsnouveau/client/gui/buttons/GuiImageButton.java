package com.hollingsworth.arsnouveau.client.gui.buttons;


import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.gui.book.BaseBook;
import com.hollingsworth.arsnouveau.client.gui.book.GuiSpellBook;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class GuiImageButton extends ANButton {
    public ResourceLocation image;

    // TODO: Remove this in favor of image
    public String resourceIcon;

    public int u, v, image_width, image_height;
    public BaseBook parent;
    public Component toolTip;
    public boolean soundDisabled = false;

    public GuiImageButton(int x, int y, int u, int v, int w, int h, int image_width, int image_height, String resource_image, Button.OnPress onPress) {
        this(x, y, u, v, w, h, image_width, image_height, new ResourceLocation(ArsNouveau.MODID, resource_image), onPress);
    }

    public GuiImageButton(int x, int y, int u, int v, int w, int h, int image_width, int image_height, ResourceLocation image, Button.OnPress onPress) {
        super(x, y, w, h, Component.literal(""), onPress);
        this.x = x;
        this.y = y;
        this.resourceIcon = image.getPath();
        this.u = u;
        this.v = v;
        this.image_height = image_height;
        this.image_width = image_width;
        this.image = image;
    }

    public GuiImageButton withTooltip(BaseBook parent, Component toolTip) {
        this.parent = parent;
        this.toolTip = toolTip;
        return this;
    }

    @Override
    public void render(GuiGraphics p_282421_,int parX, int parY, float partialTicks) {
        if (visible) {
            if (parent != null && parent.isMouseInRelativeRange(parX, parY, x, y, width, height) && toolTip != null) {
                if (!toolTip.toString().isEmpty()) {
                    List<Component> tip = new ArrayList<>();
                    tip.add(toolTip);
                    parent.tooltip = tip;
                }
            }
            GuiSpellBook.drawFromTexture(image, x, y, u, v, width, height, image_width, image_height, p_282421_);
        }
    }

    @Override
    public void playDownSound(SoundManager pHandler) {
        if (soundDisabled)
            return;
        super.playDownSound(pHandler);
    }

    public void setPosition(int pX, int pY) {
        this.x = pX;
        this.y = pY;
    }
}