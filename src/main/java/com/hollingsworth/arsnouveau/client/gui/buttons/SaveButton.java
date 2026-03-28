package com.hollingsworth.arsnouveau.client.gui.buttons;

import com.hollingsworth.arsnouveau.api.documentation.DocAssets;
import com.hollingsworth.arsnouveau.client.gui.book.BaseBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class SaveButton extends GuiImageButton {

    public SaveButton(int x, int y, OnPress onPress) {
        super(x, y, DocAssets.SAVE_ICON, onPress);
    }

    @Override
    protected void renderContents(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.renderContents(graphics, pMouseX, pMouseY, pPartialTick);
        graphics.drawString(Minecraft.getInstance().font, Component.translatable("ars_nouveau.color_gui.save"), x + 18, y + 4, BaseBook.FONT_COLOR, false);
    }
}
