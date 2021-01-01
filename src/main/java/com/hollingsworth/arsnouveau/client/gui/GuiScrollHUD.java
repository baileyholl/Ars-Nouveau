package com.hollingsworth.arsnouveau.client.gui;

import com.hollingsworth.arsnouveau.common.items.ItemScroll;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

import java.util.ArrayList;
import java.util.List;

import static com.hollingsworth.arsnouveau.common.items.ItemScroll.ITEM_PREFIX;

public class GuiScrollHUD extends AbstractGui {

    private static final Minecraft minecraft = Minecraft.getInstance();

    public void drawHUD(MatrixStack matrixStack, ItemFrameEntity entity){
        if(entity.getDisplayedItem().getItem() instanceof ItemScroll){
            ItemStack stack = entity.getDisplayedItem();
            CompoundNBT tag = stack.getTag();
            if(tag == null)
                return;
            List<ItemStack> stacks = new ArrayList<>();
            for(String s : tag.keySet()){
                if(s.contains(ITEM_PREFIX)){
                    stacks.add(ItemStack.read(tag.getCompound(s)));
                }
            }
            int offsetLeft = 5;
            fill(matrixStack, offsetLeft, 50, 100+ offsetLeft, 0, 300000);
            int counter = 0;
            for(ItemStack s : stacks){
                minecraft.fontRenderer.drawStringWithShadow(matrixStack, s.getDisplayName().getString(), offsetLeft, 5f + 10 * counter, 0xFFFFFF);
            }
        }
    }
}
