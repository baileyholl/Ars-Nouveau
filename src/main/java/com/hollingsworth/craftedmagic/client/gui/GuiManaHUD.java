package com.hollingsworth.craftedmagic.client.gui;

import com.hollingsworth.craftedmagic.capability.ManaCapability;
import com.hollingsworth.craftedmagic.items.SpellBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.item.ItemStack;

import java.awt.*;

public class GuiManaHUD extends AbstractGui {
    private static final Minecraft minecraft = Minecraft.getInstance();

    public void drawHUD() {
        ItemStack stack = minecraft.player.getHeldItemMainhand();
        if(stack != null && stack.getItem() instanceof SpellBook && stack.getTag() != null){
            ManaCapability.getMana(minecraft.player).ifPresent(mana ->{
                int offsetLeft = 10;
                double x = 100 ; //Length
                x = (x) * ((new Double(mana.getCurrentMana())) / (new Double(mana.getMaxMana()) - 0.0))  + offsetLeft;

                int y = minecraft.mainWindow.getScaledHeight() - 5;
                //int offsetLeft = minecraft.mainWindow.getScaledWidth()/2;

                int offsetStop =  minecraft.mainWindow.getScaledHeight() - 15;; // This determines the thickness from Y. Draws from Y to this value. Must be > y.
                fill((int)offsetLeft, y, (int)100+ offsetLeft, offsetStop, 0xFF000000 | Integer.parseInt("C9CAB9", 16));
                fillGradient((int)x, y, offsetLeft, offsetStop, 0xFF000000 | Integer.parseInt("337CFF", 16), new Color(0xFF000000 | Integer.parseInt("1145A1", 16)).darker().getRGB());
                //fillGradient(50, 10, 8, 5,  0xFF000000 | Integer.parseInt("DDDDDD", 16), new Color(0xFF000000 | Integer.parseInt("DDDDDD", 16)).darker().getRGB());
                for(int i = 100; i <= mana.getMaxMana(); i+=100){
                    double marker = (100) * ((i) / (new Double(mana.getMaxMana()) - 0.0))  + offsetLeft;
                    fill((int)marker, y, (int)marker+1, offsetStop, 0xFF000000 | Integer.parseInt("E4F10A", 16));
                }
            });
        }

    }
}
