package com.hollingsworth.craftedmagic.client.gui;

import com.hollingsworth.craftedmagic.items.Spell;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;


public class GuiSpellHUD extends AbstractGui {
    private static final Minecraft minecraft = Minecraft.getInstance();

    public void drawHUD() {
        ItemStack stack = minecraft.player.getHeldItemMainhand();
        if(stack != null && stack.getItem() instanceof Spell && stack.getTag() != null){
            CompoundNBT tag = stack.getTag();
            int mode = tag.getInt(Spell.BOOK_MODE_TAG);
            String renderString = "";
            if(mode != 0){
            renderString = mode + " " + Spell.getSpellName(stack.getTag());
            }else{
                renderString = "Crafting Mode";
            }
            minecraft.fontRenderer.drawStringWithShadow(renderString, 3, 3 , 0xFFFFFF);
        }
    }
}