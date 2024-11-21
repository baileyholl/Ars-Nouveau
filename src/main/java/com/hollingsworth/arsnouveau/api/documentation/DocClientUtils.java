package com.hollingsworth.arsnouveau.api.documentation;

import net.minecraft.client.gui.GuiGraphics;

public class DocClientUtils {

    public static void blit(GuiGraphics graphics, DocAssets.BlitInfo info, int x, int y){
        graphics.blit(info.location(), x, y, info.u(), info.v(), info.width(), info.height(), info.width(), info.height());
    }
}
