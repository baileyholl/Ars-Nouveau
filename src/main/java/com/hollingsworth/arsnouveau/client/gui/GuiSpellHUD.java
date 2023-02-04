package com.hollingsworth.arsnouveau.client.gui;

import com.hollingsworth.arsnouveau.api.spell.ISpellCaster;
import com.hollingsworth.arsnouveau.api.util.CasterUtil;
import com.hollingsworth.arsnouveau.api.util.StackUtil;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;


public class GuiSpellHUD {
    public static final IGuiOverlay OVERLAY = GuiSpellHUD::renderOverlay;

    private static final Minecraft minecraft = Minecraft.getInstance();

    public static void renderOverlay(ForgeGui gui, PoseStack ms, float pt, int width,
                                     int height) {
        ItemStack stack = StackUtil.getHeldSpellbook(minecraft.player);
        if (stack != ItemStack.EMPTY && stack.getItem() instanceof SpellBook && stack.getTag() != null) {
            int offsetLeft = 10;
            ISpellCaster caster = CasterUtil.getCaster(stack);
            String renderString = caster.getCurrentSlot() + 1 + " " + caster.getSpellName();
            minecraft.font.drawShadow(ms, renderString, offsetLeft, minecraft.getWindow().getGuiScaledHeight() - 30, 0xFFFFFF);
        }
    }
}