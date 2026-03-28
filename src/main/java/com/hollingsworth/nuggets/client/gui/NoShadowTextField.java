package com.hollingsworth.nuggets.client.gui;

import net.minecraft.util.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;

import javax.annotation.Nullable;
import java.util.function.Function;

public class NoShadowTextField extends CopyEditBox {

    public Function<String, Void> onClear;

    public NoShadowTextField(Font p_i232260_1_, int p_i232260_2_, int p_i232260_3_, int p_i232260_4_, int p_i232260_5_, Component p_i232260_6_) {
        super(p_i232260_1_, p_i232260_2_, p_i232260_3_, p_i232260_4_, p_i232260_5_, p_i232260_6_);
        textColor = -8355712;
    }

    public NoShadowTextField(Font p_i232259_1_, int p_i232259_2_, int p_i232259_3_, int p_i232259_4_, int p_i232259_5_, @Nullable EditBox p_i232259_6_, Component p_i232259_7_) {
        super(p_i232259_1_, p_i232259_2_, p_i232259_3_, p_i232259_4_, p_i232259_5_, p_i232259_6_, p_i232259_7_);
        textColor = -8355712;
    }

    // EditBox.applyFormat is private; replicate: iterate formatters, fall back to plain text.
    private FormattedCharSequence applyFormat(String text, int offset) {
        for (EditBox.TextFormatter f : this.formatters) {
            FormattedCharSequence result = f.format(text, offset);
            if (result != null) return result;
        }
        return FormattedCharSequence.forward(text, Style.EMPTY);
    }

    public int getXTextOffset(){
        return this.bordered ? this.getX() + 4 : this.getX();
    }

    public int getYTextOffset(){
        return this.bordered ? this.getY() + (this.height - 8) / 2 : this.getY();
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            int i2 = this.isEditable ? this.textColor : this.textColorUneditable;
            int adjustedCursorPos = this.cursorPos - this.displayPos;
            String displayValue = this.font.plainSubstrByWidth(this.value.substring(this.displayPos), this.getInnerWidth());
            boolean flag = adjustedCursorPos >= 0 && adjustedCursorPos <= displayValue.length();
            boolean flag1 = this.isFocused() && (Util.getMillis() - this.focusedTime) / 300L % 2L == 0L && flag;
            int xOffset = this.getXTextOffset();
            int yOffset = this.getYTextOffset();
            int xOffsetCopy = xOffset;

            if (!displayValue.isEmpty()) {
                String s1 = flag ? displayValue.substring(0, adjustedCursorPos) : displayValue;
                var formatted = this.applyFormat(s1, this.displayPos);
                graphics.drawString(this.font, formatted, xOffset, yOffset, textColor, false);
                xOffsetCopy = xOffset + this.font.width(formatted);
            }

            boolean flag2 = this.cursorPos < this.value.length() || this.value.length() >= 32;
            int k1 = xOffsetCopy;
            if (!flag) {
                k1 = adjustedCursorPos > 0 ? xOffset + this.width : xOffset;
            } else if (flag2) {
                k1 = xOffsetCopy - 1;
                --xOffsetCopy;
            }

            if (!displayValue.isEmpty() && flag && adjustedCursorPos < displayValue.length()) {
                graphics.drawString(this.font, this.applyFormat(displayValue.substring(adjustedCursorPos), this.cursorPos), xOffsetCopy, yOffset, i2);
            }

            if (this.value.isEmpty() && !flag2 && this.suggestion != null) {
                graphics.drawString(this.font, this.suggestion, k1, yOffset, textColor, false);
            }

            if (flag1) {
                if (flag2) {
                    graphics.fill(k1, yOffset - 1, k1 + 1, yOffset + 1 + 9, -3092272);
                } else {
                    graphics.drawString(this.font, "_", k1, yOffset, i2, false);
                }
            }
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isHandled) { // 0 for primary, 1 for secondary
        double clickedX = event.x();
        double clickedY = event.y();
        int mouseButton = event.button();
        if (!this.isVisible()) {
            return false;
        } else {
            boolean clickedThis = clickedX >= (double) this.getX() && clickedX < (double) (this.getX() + this.width) && clickedY >= (double) this.getY() && clickedY < (double) (this.getY() + this.height);
            if (this.canLoseFocus) {
                this.setFocused(clickedThis);
            }

            if (this.isFocused() && clickedThis && mouseButton == 0) {
                int i = Mth.floor(clickedX) - this.getX();
                if (this.bordered) {
                    i -= 4;
                }

                String s = this.font.plainSubstrByWidth(this.value.substring(this.displayPos), this.getInnerWidth());
                this.moveCursorTo(this.font.plainSubstrByWidth(s, i).length() + this.displayPos, true);
                return true;
            } else if (this.isFocused() && mouseButton == 1) {
                if (this.value.isEmpty())
                    return clickedThis;


                if (onClear != null)
                    onClear.apply("");
                setValue("");
                return clickedThis;
            } else {
                return false;
            }
        }
    }
}
