package com.hollingsworth.arsnouveau.client.gui;

import com.hollingsworth.arsnouveau.api.util.StackUtil;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FocusableGui;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Collections;
import java.util.List;


public class GuiSpellHUD extends FocusableGui implements IGuiEventListener {
    private static final Minecraft minecraft = Minecraft.getInstance();


    @Override
    public List<? extends IGuiEventListener> children() {
        return Collections.emptyList();
    }

    @Override
    public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
        return false;
    }

    @Override
    public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
        return false;
    }

    public void drawHUD(MatrixStack ms) {
        ItemStack stack = StackUtil.getHeldSpellbook(minecraft.player);
        if(stack != ItemStack.EMPTY && stack.getItem() instanceof SpellBook && stack.getTag() != null){
            int offsetLeft = 10;
            CompoundNBT tag = stack.getTag();
            int mode = tag.getInt(SpellBook.BOOK_MODE_TAG);
            String renderString = "";
            if(mode != 0){
            renderString = mode + " " + SpellBook.getSpellName(stack.getTag());
            }else{
                renderString = new TranslationTextComponent("ars_nouveau.spell_hud.crafting_mode").getString();
            }
            minecraft.font.drawShadow(ms,renderString, offsetLeft, minecraft.getWindow().getGuiScaledHeight() - 30 , 0xFFFFFF);
        }
    }
}