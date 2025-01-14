package com.hollingsworth.arsnouveau.client.gui.documentation;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.documentation.DocClientUtils;
import com.hollingsworth.nuggets.client.gui.NuggetImageButton;
import com.hollingsworth.nuggets.client.gui.NuggetMultilLineLabel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class DocSectionButton extends NuggetImageButton {
    public static ResourceLocation image = ArsNouveau.prefix("textures/gui/documentation/doc_button_section.png");
    public ItemStack renderItem;
    public Component title;
    private NuggetMultilLineLabel message;

    public DocSectionButton(int x, int y, Component title, ItemStack renderItem, OnPress onPress) {
        super(x, y, 118, 27, image, onPress);
        this.renderItem = renderItem;
        this.title = title;
        this.message = NuggetMultilLineLabel.create(Minecraft.getInstance().font, title, 98);
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.renderWidget(graphics, pMouseX, pMouseY, pPartialTick);
        graphics.renderItem(renderItem, x + 3, y + 3);
        DocClientUtils.drawHeader(message, graphics, x + 68, y);
    }
}
