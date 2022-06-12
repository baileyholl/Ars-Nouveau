package com.hollingsworth.arsnouveau.client.gui;

import com.hollingsworth.arsnouveau.api.spell.ISpellCaster;
import com.hollingsworth.arsnouveau.api.util.CasterUtil;
import com.hollingsworth.arsnouveau.api.util.StackUtil;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.Collections;
import java.util.List;


public class GuiSpellHUD extends AbstractContainerEventHandler implements GuiEventListener {
    private static final Minecraft minecraft = Minecraft.getInstance();


    @Override
    public List<? extends GuiEventListener> children() {
        return Collections.emptyList();
    }

    @Override
    public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
        return false;
    }

    @Override
    public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
        return false;
    }

    public void drawHUD(PoseStack ms) {
        ItemStack stack = StackUtil.getHeldSpellbook(minecraft.player);
        if(stack != ItemStack.EMPTY && stack.getItem() instanceof SpellBook && stack.getTag() != null){
            int offsetLeft = 10;
            ISpellCaster caster =  CasterUtil.getCaster(stack);
            String renderString;
            if(caster.getCurrentSlot() != 0){
                renderString = caster.getCurrentSlot() + " " + caster.getSpellName();
            }else{
                renderString = Component.translatable("ars_nouveau.spell_hud.crafting_mode").getString();
            }
            minecraft.font.drawShadow(ms,renderString, offsetLeft, minecraft.getWindow().getGuiScaledHeight() - 30 , 0xFFFFFF);
        }
    }
}