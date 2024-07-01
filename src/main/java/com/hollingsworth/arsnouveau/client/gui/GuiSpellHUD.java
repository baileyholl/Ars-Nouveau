package com.hollingsworth.arsnouveau.client.gui;

import com.hollingsworth.arsnouveau.api.spell.ISpellCaster;
import com.hollingsworth.arsnouveau.api.util.CasterUtil;
import com.hollingsworth.arsnouveau.api.util.StackUtil;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.world.item.ItemStack;


public class GuiSpellHUD {
    public static final LayeredDraw.Layer OVERLAY = GuiSpellHUD::renderOverlay;

    private static final Minecraft minecraft = Minecraft.getInstance();

    public static void renderOverlay(GuiGraphics graphics, DeltaTracker deltaTracker) {
        ItemStack stack = StackUtil.getHeldSpellbook(minecraft.player);
        if (stack != ItemStack.EMPTY && stack.getItem() instanceof SpellBook) {
            int offsetLeft = 10;
            ISpellCaster caster = CasterUtil.getCaster(stack);
            String renderString = caster.getCurrentSlot() + 1 + " " + caster.getSpellName();
            graphics.drawString(Minecraft.getInstance().font, renderString, offsetLeft, minecraft.getWindow().getGuiScaledHeight() - 30, 0xFFFFFF);
        }
    }
}