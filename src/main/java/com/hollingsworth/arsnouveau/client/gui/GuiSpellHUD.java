package com.hollingsworth.arsnouveau.client.gui;

import com.hollingsworth.arsnouveau.api.spell.ISpellCaster;
import com.hollingsworth.arsnouveau.api.util.CasterUtil;
import com.hollingsworth.arsnouveau.api.util.StackUtil;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.gui.overlay.ExtendedGui;
import net.neoforged.neoforge.client.gui.overlay.IGuiOverlay;


public class GuiSpellHUD {
    public static final IGuiOverlay OVERLAY = GuiSpellHUD::renderOverlay;

    private static final Minecraft minecraft = Minecraft.getInstance();

    public static void renderOverlay(ExtendedGui gui, GuiGraphics graphics, float pt, int width,
                                     int height) {
        ItemStack stack = StackUtil.getHeldSpellbook(minecraft.player);
        if (stack != ItemStack.EMPTY && stack.getItem() instanceof SpellBook && stack.getTag() != null) {
            int offsetLeft = 10;
            ISpellCaster caster = CasterUtil.getCaster(stack);
            String renderString = caster.getCurrentSlot() + 1 + " " + caster.getSpellName();
            graphics.drawString(gui.getFont(), renderString, offsetLeft, minecraft.getWindow().getGuiScaledHeight() - 30, 0xFFFFFF);
        }
    }
}