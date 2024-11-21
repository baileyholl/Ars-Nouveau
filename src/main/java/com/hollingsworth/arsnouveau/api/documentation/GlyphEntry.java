package com.hollingsworth.arsnouveau.api.documentation;

import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.client.gui.documentation.BaseDocScreen;
import com.hollingsworth.arsnouveau.client.gui.utils.RenderUtils;
import com.hollingsworth.nuggets.client.gui.GuiHelpers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class GlyphEntry extends TextEntry {

    AbstractSpellPart spellPart;

    public GlyphEntry(AbstractSpellPart spellPart, BaseDocScreen parent, int x, int y, int width, int height) {
        super(spellPart.getBookDescLang(), Component.literal(spellPart.getLocaleName()), spellPart.glyphItem.getDefaultInstance(), parent, x, y, width, height);
    }

    public static SinglePageCtor create(AbstractSpellPart spellPart){
        return (parent, x, y, width, height) -> new GlyphEntry(spellPart, parent, x, y, width, height);
    }


    public int drawTitle(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks){
        Font font = Minecraft.getInstance().font;
        DocClientUtils.blit(guiGraphics, DocAssets.HEADER_WITH_ITEM, x, y);
        RenderUtils.drawItemAsIcon(renderStack, guiGraphics, x + 3, y + 3, 16, false);
        GuiHelpers.drawCenteredStringNoShadow(font, guiGraphics, title, x + 70, y + 7, 0);
        DocClientUtils.blit(guiGraphics, DocAssets.GLYPH_DETAILS, x, y + 23);
        return 39;
    }
}
