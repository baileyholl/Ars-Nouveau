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
                double x = 50; //Length
                x = (50) * ((new Double(mana.getCurrentMana()) ) / (new Double(mana.getMaxMana()) - 0.0));
                int y = 15;
                int offsetLeft = 2;
                int offsetStop = 10; // This determines the thickness from Y. Draws from Y to this value. Must be > y.
                fillGradient((int)x, y, offsetLeft, offsetStop, 0xFF000000 | Integer.parseInt("FFFF55", 16), new Color(0xFF000000 | Integer.parseInt("FFFF55", 16)).darker().getRGB());
                //fillGradient(50, 10, 8, 5,  0xFF000000 | Integer.parseInt("DDDDDD", 16), new Color(0xFF000000 | Integer.parseInt("DDDDDD", 16)).darker().getRGB());
            });
        }

    }
}
