package com.hollingsworth.arsnouveau.api.documentation;

import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.SpellSchool;
import com.hollingsworth.arsnouveau.client.gui.documentation.BaseDocScreen;
import com.hollingsworth.arsnouveau.client.gui.utils.RenderUtils;
import com.hollingsworth.nuggets.client.gui.GuiHelpers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.List;

public class GlyphEntry extends TextEntry {

    public AbstractSpellPart spellPart;

    public GlyphEntry(AbstractSpellPart spellPart, BaseDocScreen parent, int x, int y, int width, int height) {
        super(spellPart.getBookDescLang(), Component.literal(spellPart.getLocaleName()), spellPart.glyphItem.getDefaultInstance(), parent, x, y, width, height);
        this.spellPart = spellPart;
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

        List<SpellSchool> schoolList = spellPart.spellSchools;
        SpellSchool firstSchool = schoolList.isEmpty() ? null : schoolList.getFirst();
        DocClientUtils.blit(guiGraphics, firstSchool == null ? DocAssets.NA_ICON : firstSchool.getIcon(), x + 2, y + 25);

        DocClientUtils.blit(guiGraphics, spellPart.getTypeIcon(), x + 15, y + 25);

        DocAssets.BlitInfo tier = switch (spellPart.getConfigTier().value){
            case 1 -> DocAssets.TIER_ONE;
            case 2 -> DocAssets.TIER_TWO;
            case 3 -> DocAssets.TIER_THREE;
            default -> DocAssets.TIER_ONE;
        };
        DocClientUtils.blit(guiGraphics, tier, x + 26, y + 25);

        int augmentCount = 0;
        for(AbstractSpellPart spellPart1 : spellPart.compatibleAugments){
            RenderUtils.drawItemAsIcon(spellPart1.glyphItem.getDefaultInstance(), guiGraphics, x + 33 + (augmentCount * 11), y + 22, 10, false);
            augmentCount++;
        }
        return 39;
    }
}
