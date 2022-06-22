package com.hollingsworth.arsnouveau.common.compat;

import net.minecraft.client.Minecraft;
import vazkii.patchouli.client.book.gui.GuiBookEntry;

public class PatchouliHandler {

    public static boolean isPatchouliWorld(){
        return Minecraft.getInstance().screen instanceof GuiBookEntry;
    }

}
