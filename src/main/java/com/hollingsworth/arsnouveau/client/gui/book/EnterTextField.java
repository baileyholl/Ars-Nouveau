package com.hollingsworth.arsnouveau.client.gui.book;

import com.hollingsworth.arsnouveau.api.documentation.DocAssets;
import com.hollingsworth.arsnouveau.api.documentation.DocClientUtils;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

/**
 * Spell name input field — draws ENTER_TEXT_PAPER background, no EditBox border.
 * Extends EditBox directly to use the MC 1.21.11 deferred text rendering pipeline.
 */
public class EnterTextField extends EditBox {

    public EnterTextField(Font font, int x, int y) {
        super(font, x, y, DocAssets.ENTER_TEXT_PAPER.width(), DocAssets.ENTER_TEXT_PAPER.height(), Component.empty());
        setBordered(false);
        setTextColor(0xFFC1CF93);
        textShadow = false;
        setSuggestion(Component.translatable("ars_nouveau.spell_book_gui.spell_name").getString());
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        DocClientUtils.blit(graphics, DocAssets.ENTER_TEXT_PAPER, x, y);
        // Custom text position: matches original NoShadowTextField bordered(4) + 11px visual indent
        this.textX = this.getX() + 15;
        this.textY = this.getY() + (this.height - 8) / 2 + 1;
        super.renderWidget(graphics, mouseX, mouseY, partialTicks);
    }
}
