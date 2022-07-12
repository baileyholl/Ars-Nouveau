package com.hollingsworth.arsnouveau.client.gui;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.client.IDisplayMana;
import com.hollingsworth.arsnouveau.api.mana.IManaCap;
import com.hollingsworth.arsnouveau.api.util.ManaUtil;
import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.common.capability.CapabilityRegistry;
import com.hollingsworth.arsnouveau.setup.Config;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class GuiManaHUD extends GuiComponent {
    private static final Minecraft minecraft = Minecraft.getInstance();

    public boolean shouldDisplayBar() {
        ItemStack mainHand = minecraft.player.getMainHandItem();
        ItemStack offHand = minecraft.player.getOffhandItem();
        return (mainHand.getItem() instanceof IDisplayMana iDisplayMana && iDisplayMana.shouldDisplay(mainHand))
                || (offHand.getItem() instanceof IDisplayMana iDisplayManaOffhand && iDisplayManaOffhand.shouldDisplay(offHand))
                || (ManaUtil.getMaxMana(minecraft.player) > ManaUtil.getCurrentMana(minecraft.player));
    }

    public void drawHUD(PoseStack ms, float pt) {
        if (!shouldDisplayBar())
            return;

        IManaCap mana = CapabilityRegistry.getMana(minecraft.player).orElse(null);
        if (mana == null)
            return;

        int offsetLeft = 10 + Config.MANABAR_X_OFFSET.get();
        int manaLength = 96;
        manaLength = (int) ((manaLength) * ((mana.getCurrentMana()) / ((double) mana.getMaxMana() - 0.0)));

        int yOffset = minecraft.getWindow().getGuiScaledHeight() - 5 + Config.MANABAR_Y_OFFSET.get();

        RenderSystem.setShaderTexture(0, new ResourceLocation(ArsNouveau.MODID, "textures/gui/manabar_gui_border.png"));
        blit(ms, offsetLeft, yOffset - 18, 0, 0, 108, 18, 256, 256);
        int manaOffset = (int) (((ClientInfo.ticksInGame + pt) / 3 % (33))) * 6;

        // 96
        RenderSystem.setShaderTexture(0, new ResourceLocation(ArsNouveau.MODID, "textures/gui/manabar_gui_mana.png"));
        blit(ms, offsetLeft + 9, yOffset - 9, 0, manaOffset, manaLength, 6, 256, 256);

        RenderSystem.setShaderTexture(0, new ResourceLocation(ArsNouveau.MODID, "textures/gui/manabar_gui_border.png"));
        blit(ms, offsetLeft, yOffset - 17, 0, 18, 108, 20, 256, 256);
    }
}
