package com.hollingsworth.craftedmagic.client.gui;

import com.hollingsworth.craftedmagic.items.SpellBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;


public class GuiSpellHUD extends AbstractGui {
    private static final Minecraft minecraft = Minecraft.getInstance();

    public void drawHUD() {
        ItemStack stack = minecraft.player.getHeldItemMainhand();
        if(stack != null && stack.getItem() instanceof SpellBook && stack.getTag() != null){
            CompoundNBT tag = stack.getTag();
            int mode = tag.getInt(SpellBook.BOOK_MODE_TAG);
            String renderString = "";
            if(mode != 0){
            renderString = mode + " " + SpellBook.getSpellName(stack.getTag());
            }else{
                renderString = "Crafting Mode";
            }
            minecraft.fontRenderer.drawStringWithShadow(renderString, 3, 3 , 0xFFFFFF);
        }
    }
}