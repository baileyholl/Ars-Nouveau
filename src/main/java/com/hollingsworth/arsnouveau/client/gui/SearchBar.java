package com.hollingsworth.arsnouveau.client.gui;

import com.hollingsworth.arsnouveau.api.documentation.DocAssets;
import com.hollingsworth.arsnouveau.api.documentation.DocClientUtils;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import java.util.function.Function;

/**
 * Search field for the spell book — draws SEARCH_PAPER background, no EditBox border.
 * Extends EditBox directly to use the MC 1.21.11 deferred text rendering pipeline.
 * Right-click clears the field and calls onClear.
 */
public class SearchBar extends EditBox {

    /** Called with "" when the field is cleared via right-click or programmatically. */
    public Function<String, Void> onClear;

    public SearchBar(Font font, int x, int y) {
        super(font, x, y, DocAssets.SEARCH_PAPER.width(), DocAssets.SEARCH_PAPER.height(), Component.empty());
        setBordered(false);
        setTextColor(0xFFC1CF93);
        textShadow = false;
        setSuggestion(Component.translatable("ars_nouveau.spell_book_gui.search").getString());
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        DocClientUtils.blit(graphics, DocAssets.SEARCH_PAPER, x, y);
        // Custom text position: matches original NoShadowTextField bordered(4) + 9px visual indent
        this.textX = this.getX() + 13;
        this.textY = this.getY() + (this.height - 8) / 2;
        super.renderWidget(graphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isHandled) {
        double cx = event.x(), cy = event.y();
        boolean hit = cx >= getX() && cx < getX() + width && cy >= getY() && cy < getY() + height;
        // Right-click: clear the field
        if (event.button() == 1 && hit) {
            if (!value.isEmpty()) {
                if (onClear != null) onClear.apply("");
                setValue("");
            }
            return true;
        }
        return super.mouseClicked(event, isHandled);
    }
}
