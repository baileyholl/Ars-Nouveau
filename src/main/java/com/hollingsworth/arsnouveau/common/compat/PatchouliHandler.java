package com.hollingsworth.arsnouveau.common.compat;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.client.book.gui.GuiBookEntry;

public class PatchouliHandler {

    public static void openBookGUI(ServerPlayer player) {
        PatchouliAPI.get().openBookGUI(player, ArsNouveau.prefix("worn_notebook"));
    }

    public static void openBookClient() {
        PatchouliAPI.get().openBookGUI(BuiltInRegistries.ITEM.getKey(ItemsRegistry.WORN_NOTEBOOK.asItem()));
    }

    public static boolean isPatchouliWorld() {
        if (!ArsNouveau.patchouliLoaded) {
            return false;
        }
        return Minecraft.getInstance().screen instanceof GuiBookEntry;
    }

}
