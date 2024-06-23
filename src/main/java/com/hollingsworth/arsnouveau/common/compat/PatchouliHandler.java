package com.hollingsworth.arsnouveau.common.compat;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.server.level.ServerPlayer;

//TODO: reenable patchouli
public class PatchouliHandler {

    public static void openBookGUI(ServerPlayer player) {
//        PatchouliAPI.get().openBookGUI(player, ArsNouveau.prefix( "worn_notebook"));
    }

    public static void openBookClient(){
//        PatchouliAPI.get().openBookGUI(ForgeRegistries.ITEMS.getKey(ItemsRegistry.WORN_NOTEBOOK.asItem()));
    }

    public static boolean isPatchouliWorld() {
        if(!ArsNouveau.patchouliLoaded){
            return false;
        }
        return false;
//        return Minecraft.getInstance().screen instanceof GuiBookEntry;
    }

}
