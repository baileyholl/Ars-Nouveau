package com.hollingsworth.arsnouveau.client.gui;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.compat.PatchouliHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.net.URI;
import java.net.URISyntaxException;

public class GuiUtils {

    public static boolean isMouseInRelativeRange(int mouseX, int mouseY, AbstractWidget widget){
        return isMouseInRelativeRange(mouseX, mouseY, widget.x, widget.y, widget.getWidth(), widget.getHeight());
    }

    public static boolean isMouseInRelativeRange(int mouseX, int mouseY, int x, int y, int w, int h) {
        return mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h;
    }

    public static void openWiki(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            if (ArsNouveau.patchouliLoaded) {
                PatchouliHandler.openBookGUI(serverPlayer);
            }
            return;
        }

        if (ArsNouveau.patchouliLoaded) {
            PatchouliHandler.openBookClient();
            return;
        }

        ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.curseforge.com/minecraft/mc-mods/patchouli");
        Component text = Component.translatable("ars_nouveau.missing.patchouli")
                .withStyle(Style.EMPTY.withColor(ChatFormatting.GOLD).withClickEvent(clickEvent));
        player.sendSystemMessage(text);

        try {
            Util.getPlatform().openUri(new URI("https://www.arsnouveau.wiki/"));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
