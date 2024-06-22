package com.hollingsworth.arsnouveau.client.gui;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.client.IDisplayMana;
import com.hollingsworth.arsnouveau.api.mana.IManaCap;
import com.hollingsworth.arsnouveau.api.util.ManaUtil;
import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.client.gui.utils.RenderUtils;
import com.hollingsworth.arsnouveau.setup.config.Config;
import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.gui.overlay.ExtendedGui;
import net.neoforged.neoforge.client.gui.overlay.IGuiOverlay;

public class GuiManaHUD {
    public static final IGuiOverlay OVERLAY = GuiManaHUD::renderOverlay;

    private static final Minecraft minecraft = Minecraft.getInstance();

    public static boolean shouldDisplayBar() {
        ItemStack mainHand = minecraft.player.getMainHandItem();
        ItemStack offHand = minecraft.player.getOffhandItem();
        return (mainHand.getItem() instanceof IDisplayMana iDisplayMana && iDisplayMana.shouldDisplay(mainHand))
                || (offHand.getItem() instanceof IDisplayMana iDisplayManaOffhand && iDisplayManaOffhand.shouldDisplay(offHand))
                || (ManaUtil.getMaxMana(minecraft.player) > ManaUtil.getCurrentMana(minecraft.player));
    }

    public static void renderOverlay(ExtendedGui gui, GuiGraphics guiGraphics, float pt, int width,
                                     int height) {
        if (!shouldDisplayBar())
            return;
        PoseStack ms = guiGraphics.pose();
        IManaCap mana = CapabilityRegistry.getMana(minecraft.player).orElse(null);
        if(mana == null){
            return;
        }
        int maxMana = mana.getMaxMana();
        if (maxMana == 0)
            return;

        int offsetLeft = 10 + Config.MANABAR_X_OFFSET.get();
        int manaLength = 96;

        manaLength *= (mana.getCurrentMana() / (maxMana * (1.0 + ClientInfo.reservedOverlayMana)));

        int yOffset = minecraft.getWindow().getGuiScaledHeight() - 5 + Config.MANABAR_Y_OFFSET.get();

        guiGraphics.blit( ArsNouveau.prefix( "textures/gui/manabar_gui_border.png"), offsetLeft, yOffset - 18, 0, 0, 108, 18, 256, 256);
        int manaOffset = (int) (((ClientInfo.ticksInGame + pt) / 3 % (33))) * 6;

        guiGraphics.blit(ArsNouveau.prefix( "textures/gui/manabar_gui_mana.png"), offsetLeft + 9, yOffset - 9, 0, manaOffset, manaLength, 6, 256, 256);

        renderReserveOverlay(ms, offsetLeft, yOffset, manaOffset, maxMana);
        renderRedOverlay(ms, offsetLeft, yOffset, manaOffset, maxMana);

        if (ArsNouveauAPI.ENABLE_DEBUG_NUMBERS) {
            String text = (int) mana.getCurrentMana() + "  /  " + maxMana;
            int maxWidth = minecraft.font.width(maxMana + "  /  " + maxMana);
            int offset = 67 - maxWidth / 2 + (maxWidth - minecraft.font.width(text));

            guiGraphics.drawString(minecraft.font, text, offset, yOffset - 10, 0xFFFFFF);
            // reserved mana text
            // guiGraphics.drawString(minecraft.font, String.valueOf((int)(ClientInfo.reservedOverlayMana * maxMana)), offset + 69, yOffset - 20, 0xFFFFFF);
        }

        guiGraphics.blit(ArsNouveau.prefix( "textures/gui/manabar_gui_border.png"), offsetLeft, yOffset - 17, 0, 18, 108, 20, 256, 256);
    }

    public static void renderRedOverlay(PoseStack ms, int offsetLeft, int yOffset, int manaOffset, int maxMana) {
        if (!ClientInfo.redTicks())
            return;

        int redManaLength = (int) (98F * Mth.clamp(0F,ClientInfo.redOverlayMana / maxMana , 1F));
        RenderSystem.setShaderTexture(0, ArsNouveau.prefix( "textures/gui/manabar_gui_grayscale.png"));
        RenderUtil.colorBlit(ms, offsetLeft + 8, yOffset - 10, 0, manaOffset, redManaLength, 8, 256, 256, Color.RED.scaleAlpha(ClientInfo.redOverlayTicks/35f));

    }

    public final static Color BLACK = new Color(35, 35, 35).setImmutable();

    static boolean stillBar = true;

    public static void renderReserveOverlay(PoseStack ms, int offsetLeft, int yOffset, int manaOffset, int maxMana){
        if (ClientInfo.reservedOverlayMana <= 0)
            return;
        int reserveManaLength = (int) (96F * ClientInfo.reservedOverlayMana);
        //invert offsets so it aligns with the right side of the bar
        int offset = 96 - reserveManaLength;
        RenderSystem.setShaderTexture(0, ArsNouveau.prefix( "textures/gui/manabar_gui_mana.png"));
        RenderUtil.colorBlit(ms, offsetLeft + 10 + offset, yOffset - 10, 0, stillBar ? 0 : manaOffset, reserveManaLength, 8, 256, 256, BLACK);

    }

}
