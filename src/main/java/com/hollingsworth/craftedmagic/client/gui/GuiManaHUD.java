package com.hollingsworth.craftedmagic.client.gui;

import com.hollingsworth.craftedmagic.items.Spell;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

import java.awt.*;

public class GuiManaHUD extends AbstractGui {
    private static final Minecraft minecraft = Minecraft.getInstance();

    public void drawHUD() {
        ItemStack stack = minecraft.player.getHeldItemMainhand();
        if(stack != null && stack.getItem() instanceof Spell && stack.getTag() != null){

            fillGradient(50, 3, 10, 5, 0xFF000000 | Integer.parseInt("FFFF55", 16), new Color(0xFF000000 | Integer.parseInt("FFFF55", 16)).darker().getRGB());
            fillGradient(50, 3, 8, 5,  0xFF000000 | Integer.parseInt("DDDDDD", 16), new Color(0xFF000000 | Integer.parseInt("DDDDDD", 16)).darker().getRGB());
        }

    }
}
