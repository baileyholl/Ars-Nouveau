package com.hollingsworth.arsnouveau.client.gui.buttons;

import com.hollingsworth.arsnouveau.api.documentation.DocAssets;
import com.hollingsworth.arsnouveau.client.gui.book.BaseBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class ClearButton extends GuiImageButton{

    public Component message;
    public ClearButton(int x, int y, Component message, OnPress onPress) {
        super(x, y, DocAssets.CLEAR_BUTTON, onPress);
        this.message = message;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.renderWidget(graphics, pMouseX, pMouseY, pPartialTick);
        graphics.drawString(Minecraft.getInstance().font, message, x + 16, y + 4, BaseBook.FONT_COLOR, false);
    }
}
