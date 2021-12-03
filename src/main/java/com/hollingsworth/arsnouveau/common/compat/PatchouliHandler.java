package com.hollingsworth.arsnouveau.common.compat;

import net.minecraft.client.Minecraft;
//import vazkii.patchouli.client.book.gui.GuiBookEntry;

public class PatchouliHandler {
    //TODO: Restore patchouli
    public static boolean isPatchouliWorld(){
        return false;//Minecraft.getInstance().screen instanceof GuiBookEntry;
    }
}
