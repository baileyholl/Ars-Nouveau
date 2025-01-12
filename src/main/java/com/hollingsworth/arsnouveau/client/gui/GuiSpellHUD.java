package com.hollingsworth.arsnouveau.client.gui;

import com.hollingsworth.arsnouveau.api.registry.SpellCasterRegistry;
import com.hollingsworth.arsnouveau.api.spell.AbstractCaster;
import com.hollingsworth.arsnouveau.api.util.StackUtil;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import com.hollingsworth.arsnouveau.setup.config.Config;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.world.item.ItemStack;


public class GuiSpellHUD {
    public static final LayeredDraw.Layer OVERLAY = GuiSpellHUD::renderOverlay;

    private static final Minecraft minecraft = Minecraft.getInstance();

    public static void renderOverlay(GuiGraphics graphics, DeltaTracker deltaTracker) {
        if (Minecraft.getInstance().options.hideGui) {
            return;
        }
        ItemStack stack = StackUtil.getHeldSpellbook(minecraft.player);
        if (stack != ItemStack.EMPTY && stack.getItem() instanceof SpellBook) {
            int offsetLeft = Config.SPELLNAME_X_OFFSET.get();
            AbstractCaster<?> caster = SpellCasterRegistry.from(stack);
            String renderString = caster.getCurrentSlot() + 1 + " " + caster.getSpellName();
            graphics.drawString(Minecraft.getInstance().font, renderString, offsetLeft, minecraft.getWindow().getGuiScaledHeight() - Config.SPELLNAME_Y_OFFSET.get(), 0xFFFFFF);
        }
    }
}