package com.hollingsworth.arsnouveau.client.gui;

import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.function.Function;

public class NoShadowTextField extends EditBox {

    public Function<String, Void> onClear;

    public NoShadowTextField(Font font, int x, int y, int width, int height, Component message) {
        super(font, x, y, width, height, message);
    }

    public NoShadowTextField(Font font, int x, int y, int width, int height, @Nullable EditBox editBox, Component message) {
        super(font, x, y, width, height, editBox, message);
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if (!this.visible) {
            return;
        }

        int textColor = this.isEditable ? this.textColor : this.textColorUneditable;
        int offset = this.cursorPos - this.displayPos;
        String s = this.font.plainSubstrByWidth(this.value.substring(this.displayPos), this.getInnerWidth());
        int selectionVisualEnd = Mth.clamp(this.highlightPos - this.displayPos, 0, s.length());
        boolean offsetInBounds = offset >= 0 && offset <= s.length();
        boolean shouldFlash = this.isFocused() && (Util.getMillis() - this.focusedTime) / 300L % 2L == 0L && offsetInBounds;
        int xStart = this.bordered ? this.x + 4 : this.x;
        int yStart = this.bordered ? this.y + (this.height - 8) / 2 : this.y;
        int textStartX = xStart;

        if (!s.isEmpty()) {
            String s1 = offsetInBounds ? s.substring(0, offset) : s;
            // 1.21.11: EditBox.formatter (BiFunction) replaced by formatters (List<TextFormatter>).
            // 1.21.11: drawString() returns void (not int); compute end X via font.width() instead.
            graphics.drawString(font, applyFormatters(s1, this.displayPos), xStart, yStart, -8355712, false);
            textStartX = xStart + this.font.width(s1);
        }

        boolean outOfSpace = this.font.width(s + "_") > this.width;
        int decorationStartX = textStartX;
        if (!offsetInBounds) {
            decorationStartX = offset > 0 ? xStart + this.width : xStart;
        } else if (outOfSpace) {
            decorationStartX = textStartX - 1;
            --textStartX;
        }

        if (!s.isEmpty() && offsetInBounds && offset < s.length()) {
            graphics.drawString(font, applyFormatters(s.substring(offset), this.cursorPos), textStartX, yStart, textColor);
        }

        if (!outOfSpace && this.suggestion != null && (this.value == null || this.value.isEmpty())) {
            graphics.drawString(this.font, this.suggestion, decorationStartX - 1, yStart, -8355712, false);
        }

        if (shouldFlash) {
            if (outOfSpace) {
                graphics.fill(decorationStartX, yStart - 1, decorationStartX + 1, yStart + 1 + 9, -3092272);
            } else {
                graphics.drawString(this.font, "_", decorationStartX, yStart, textColor, false);
            }
        }

        if (selectionVisualEnd != offset) {
            int selectionVisualEndX = xStart + this.font.width(s.substring(0, selectionVisualEnd));
            this.renderHighlight(graphics, decorationStartX, yStart - 1, selectionVisualEndX - 1, yStart + 9);
        }
    }

    /**
     * 1.21.11: EditBox.formatter (single BiFunction) replaced by formatters (List of TextFormatter).
     * Applies all registered formatters in sequence; falls back to plain text if the list is empty.
     */
    private FormattedCharSequence applyFormatters(String text, int cursorOffset) {
        FormattedCharSequence result = FormattedCharSequence.forward(text, Style.EMPTY);
        for (EditBox.TextFormatter fmt : this.formatters) {
            FormattedCharSequence formatted = fmt.format(text, cursorOffset);
            if (formatted != null) {
                result = formatted;
            }
        }
        return result;
    }

    private void renderHighlight(GuiGraphics guiGraphics, int minX, int minY, int maxX, int maxY) {
        if (minX < maxX) {
            int i = minX;
            minX = maxX;
            maxX = i;
        }

        if (minY < maxY) {
            int j = minY;
            minY = maxY;
            maxY = j;
        }

        if (maxX > this.getX() + this.width) {
            maxX = this.getX() + this.width;
        }

        if (minX > this.getX() + this.width) {
            minX = this.getX() + this.width;
        }

        guiGraphics.textHighlight(minX, minY, maxX, maxY, false);
    }

    // 1.21.11: mouseClicked signature changed from (double, double, int) to (MouseButtonEvent, boolean).
    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean consumed) {
        double clickedX = event.x();
        double clickedY = event.y();
        int mouseButton = event.button();
        if (!this.isVisible()) {
            return false;
        } else {
            boolean clickedThis = clickedX >= (double) this.x && clickedX < (double) (this.x + this.width) && clickedY >= (double) this.y && clickedY < (double) (this.y + this.height);
            if (this.canLoseFocus) {
                this.setFocused(clickedThis);
            }

            if (this.isFocused() && clickedThis && mouseButton == 0) {
                int i = Mth.floor(clickedX) - this.x;
                if (this.bordered) {
                    i -= 4;
                }

                String s = this.font.plainSubstrByWidth(this.value.substring(this.displayPos), this.getInnerWidth());
                this.moveCursorTo(this.font.plainSubstrByWidth(s, i).length() + this.displayPos, true);
                return true;
            } else if (this.isFocused() && mouseButton == 1) {
                if (this.value.isEmpty()) {
                    return clickedThis;
                }

                if (onClear != null) {
                    onClear.apply("");
                }

                setValue("");
                return clickedThis;
            } else {
                return false;
            }
        }
    }
}
