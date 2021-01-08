package com.hollingsworth.arsnouveau.client.gui;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.client.IDisplayMana;
import com.hollingsworth.arsnouveau.api.mana.IMana;
import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.common.capability.ManaCapability;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class GuiManaHUD extends AbstractGui {
    private static final Minecraft minecraft = Minecraft.getInstance();

    public boolean shouldDisplayBar(){
        ItemStack mainHand = minecraft.player.getHeldItemMainhand();
        ItemStack offHand = minecraft.player.getHeldItemOffhand();
        return (mainHand.getItem() instanceof IDisplayMana && ((IDisplayMana) mainHand.getItem()).shouldDisplay(mainHand))
                || (offHand.getItem() instanceof IDisplayMana && ((IDisplayMana) offHand.getItem()).shouldDisplay(offHand));
    }

    public void drawHUD(MatrixStack ms, float pt) {
        if(!shouldDisplayBar())
            return;

        IMana mana = ManaCapability.getMana(minecraft.player).orElse(null);
        if(mana == null)
            return;

        int offsetLeft = 10;
        int manaLength = 96;
        manaLength = (int) ((manaLength) * ((mana.getCurrentMana()) / ((double) mana.getMaxMana() - 0.0)));

        int height = minecraft.getMainWindow().getScaledHeight() - 5;

        Minecraft.getInstance().textureManager.bindTexture(new ResourceLocation(ArsNouveau.MODID, "textures/gui/manabar_gui_border.png"));
        blit(ms,offsetLeft, height - 18, 0, 0, 108, 18, 256, 256);
        int manaOffset = (int) (((ClientInfo.ticksInGame + pt) / 3 % (33))) * 6;

        // 96
        Minecraft.getInstance().textureManager.bindTexture(new ResourceLocation(ArsNouveau.MODID, "textures/gui/manabar_gui_mana.png"));
        blit(ms,offsetLeft + 9, height - 9, 0, manaOffset, manaLength,6, 256, 256);

        Minecraft.getInstance().textureManager.bindTexture(new ResourceLocation(ArsNouveau.MODID, "textures/gui/manabar_gui_border.png"));
        blit(ms,offsetLeft, height - 17, 0, 18, 108, 20, 256, 256);
    }
}
