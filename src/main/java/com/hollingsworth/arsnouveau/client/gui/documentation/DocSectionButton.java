package com.hollingsworth.arsnouveau.client.gui.documentation;

import com.hollingsworth.arsnouveau.api.documentation.DocAssets;
import com.hollingsworth.arsnouveau.api.documentation.DocClientUtils;
import com.hollingsworth.nuggets.client.gui.NuggetImageButton;
import com.hollingsworth.nuggets.client.gui.NuggetMultilLineLabel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class DocSectionButton extends NuggetImageButton {
    public ItemStack renderItem;
    public Component title;
    private NuggetMultilLineLabel message;

    public DocSectionButton(int x, int y, Component title, ItemStack renderItem, OnPress onPress) {
        super(x, y, DocAssets.CHAPTER_BUTTON_FRAME.width(), DocAssets.CHAPTER_BUTTON_FRAME.height(), DocAssets.CHAPTER_BUTTON_FRAME.location(), onPress);
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
