package com.hollingsworth.arsnouveau.client.gui;

import com.hollingsworth.arsnouveau.common.items.ItemScroll;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static com.hollingsworth.arsnouveau.common.items.ItemScroll.ITEM_PREFIX;

public class GuiScrollHUD extends GuiComponent {

    private static final Minecraft minecraft = Minecraft.getInstance();

    public void drawHUD(PoseStack matrixStack, ItemFrame entity) {
        if (entity.getItem().getItem() instanceof ItemScroll) {
            ItemStack stack = entity.getItem();
            CompoundTag tag = stack.getTag();
            if (tag == null)
                return;
            List<ItemStack> stacks = new ArrayList<>();
            for (String s : tag.getAllKeys()) {
                if (s.contains(ITEM_PREFIX)) {
                    stacks.add(ItemStack.of(tag.getCompound(s)));
                }
            }
            int offsetLeft = 5;
            fill(matrixStack, offsetLeft, 50, 100 + offsetLeft, 0, 300000);
            int counter = 0;
            for (ItemStack s : stacks) {
                minecraft.font.drawShadow(matrixStack, s.getHoverName().getString(), offsetLeft, 5f + 10 * counter, 0xFFFFFF);
                counter++;
            }
        }
    }
}
