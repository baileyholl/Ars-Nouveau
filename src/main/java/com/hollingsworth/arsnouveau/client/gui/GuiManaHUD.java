package com.hollingsworth.arsnouveau.client.gui;

import com.hollingsworth.arsnouveau.api.util.StackUtil;
import com.hollingsworth.arsnouveau.common.capability.ManaCapability;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.item.ItemStack;

import java.awt.*;

public class GuiManaHUD extends AbstractGui {
    private static final Minecraft minecraft = Minecraft.getInstance();

    public void drawHUD(MatrixStack ms) {
        ItemStack stack = StackUtil.getHeldSpellbook(minecraft.player);
        if(stack != null && stack.getItem() instanceof SpellBook && stack.getTag() != null){
            ManaCapability.getMana(minecraft.player).ifPresent(mana ->{
                int offsetLeft = 10;
                double x = 100 ; //Length
                x = (x) * ((new Double(mana.getCurrentMana())) / (new Double(mana.getMaxMana()) - 0.0))  + offsetLeft;

                int y = minecraft.getMainWindow().getScaledHeight() - 5;
                //int offsetLeft = minecraft.mainWindow.getScaledWidth()/2;

                int offsetStop =  minecraft.getMainWindow().getScaledHeight() - 15;; // This determines the thickness from Y. Draws from Y to this value. Must be > y.
                fill(ms,(int)offsetLeft, y, (int)100+ offsetLeft, offsetStop, 0xFF000000 | Integer.parseInt("C9CAB9", 16));
                fillGradient(ms,(int)x, y, offsetLeft, offsetStop, 0xFF000000 | Integer.parseInt("337CFF", 16), new Color(0xFF000000 | Integer.parseInt("1145A1", 16)).darker().getRGB());
                //fillGradient(50, 10, 8, 5,  0xFF000000 | Integer.parseInt("DDDDDD", 16), new Color(0xFF000000 | Integer.parseInt("DDDDDD", 16)).darker().getRGB());
                for(int i = 100; i <= mana.getMaxMana(); i+=100){
                    double marker = (100) * ((i) / (new Double(mana.getMaxMana()) - 0.0))  + offsetLeft;
                    fill(ms,(int)marker, y, (int)marker+1, offsetStop, 0xFF000000 | Integer.parseInt("E4F10A", 16));
                }
            });
        }

    }
}
