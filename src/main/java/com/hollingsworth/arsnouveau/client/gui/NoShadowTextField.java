package com.hollingsworth.arsnouveau.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import javax.annotation.Nullable;
import java.util.function.Function;

public class NoShadowTextField extends EditBox {

    public Function<String, Void> onClear;

    public NoShadowTextField(Font p_i232260_1_, int p_i232260_2_, int p_i232260_3_, int p_i232260_4_, int p_i232260_5_, Component p_i232260_6_) {
        super(p_i232260_1_, p_i232260_2_, p_i232260_3_, p_i232260_4_, p_i232260_5_, p_i232260_6_);
    }

    public NoShadowTextField(Font p_i232259_1_, int p_i232259_2_, int p_i232259_3_, int p_i232259_4_, int p_i232259_5_, @Nullable EditBox p_i232259_6_, Component p_i232259_7_) {
        super(p_i232259_1_, p_i232259_2_,p_i232259_3_,p_i232259_4_,p_i232259_5_, p_i232259_6_,p_i232259_7_);
    }

    @Override
    public void renderButton(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (this.isVisible()) {
//            if (this.getEnableBackgroundDrawing()) {
//                int i = this.isFocused() ? -1 : -6250336;
//                fill(matrixStack, this.x - 1, this.y - 1, this.x + this.width + 1, this.y + this.height + 1, i);
//                fill(matrixStack, this.x, this.y, this.x + this.width, this.y + this.height, -16777216);
//            }

            int i2 = this.isEditable ? this.textColor : this.textColorUneditable;
            int j = this.cursorPos - this.displayPos;
            int k = this.highlightPos - this.displayPos;
            String s = this.font.plainSubstrByWidth(this.value.substring(this.displayPos), this.getInnerWidth());
            boolean flag = j >= 0 && j <= s.length();
            boolean flag1 = this.isFocused() && this.frame / 6 % 2 == 0 && flag;
            int l = this.bordered ? this.x + 4 : this.x;
            int i1 = this.bordered ? this.y + (this.height - 8) / 2 : this.y;
            int j1 = l;
            if (k > s.length()) {
                k = s.length();
            }

            if (!s.isEmpty()) {
                String s1 = flag ? s.substring(0, j) : s;
                j1 = this.font.draw(matrixStack,s1, (float)l, (float)i1,  -8355712);
            }

            boolean flag2 = this.cursorPos < this.value.length() || this.value.length() >= 32;
            int k1 = j1;
            if (!flag) {
                k1 = j > 0 ? l + this.width : l;
            } else if (flag2) {
                k1 = j1 - 1;
                --j1;
            }

            if (!s.isEmpty() && flag && j < s.length()) {
                this.font.draw(matrixStack, s.substring(j), (float)j1, (float)i1, i2);
            }

            if (!flag2 && this.suggestion != null) {
                this.font.draw(matrixStack, this.suggestion, (float)(k1 - 1), (float)i1, -8355712);
            }

            if (flag1) {
                if (flag2) {
                    GuiComponent.fill(matrixStack, k1, i1 - 1, k1 + 1, i1 + 1 + 9, -3092272);
                } else {
                    this.font.draw(matrixStack, "_", (float)k1, (float)i1, i2);
                }
            }

//            if (k != j) {
//                int l1 = l + this.fontRenderer.getStringWidth(s.substring(0, k));
//                this.drawSelectionBox(k1, i1 - 1, l1 - 1, i1 + 1 + 9);
//            }

        }
    }

    @Override
    public boolean mouseClicked(double clickedX, double clickedY, int mouseButton) { // 0 for primary, 1 for secondary
        if (!this.isVisible()) {
            return false;
        } else {
            boolean flag = clickedX >= (double)this.x && clickedX < (double)(this.x + this.width) && clickedY >= (double)this.y && clickedY < (double)(this.y + this.height);
            if (this.canLoseFocus) {
                this.setFocus(flag);
            }

            if (this.isFocused() && flag && mouseButton == 0) {
                int i = Mth.floor(clickedX) - this.x;
                if (this.bordered) {
                    i -= 4;
                }

                String s = this.font.plainSubstrByWidth(this.value.substring(this.displayPos), this.getInnerWidth());
                this.moveCursorTo(this.font.plainSubstrByWidth(s, i).length() + this.displayPos);
                return true;
            }else if(this.isFocused() && mouseButton == 1){
                if(this.value.isEmpty())
                    return false;


                if(onClear != null)
                    onClear.apply("");
                setValue("");
                return true;
            }
            else {
                return false;
            }
        }
    }
}
