package com.hollingsworth.arsnouveau.client.gui;

import com.hollingsworth.arsnouveau.api.documentation.DocAssets;
import com.hollingsworth.arsnouveau.api.documentation.DocClientUtils;
import com.hollingsworth.nuggets.client.gui.NoShadowTextField;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class SearchBar extends NoShadowTextField {

    public SearchBar(Font font, int x, int y) {
        super(font, x, y,  DocAssets.SEARCH_PAPER.width(),  DocAssets.SEARCH_PAPER.height(), null, Component.empty());
        setTextColor(12694931);
        setSuggestion(Component.translatable("ars_nouveau.spell_book_gui.search").getString());
    }

    @Override
    public int getXTextOffset() {
        return super.getXTextOffset() + 8;
    }

    @Override
    public int getYTextOffset() {
        return super.getYTextOffset() + 1;
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        DocClientUtils.blit(graphics, DocAssets.SEARCH_PAPER, x, y);
        super.renderWidget(graphics, mouseX, mouseY, partialTicks);
    }
}
