package com.hollingsworth.arsnouveau.api.documentation.entry;

import com.google.gson.JsonObject;
import com.hollingsworth.arsnouveau.api.documentation.DocAssets;
import com.hollingsworth.arsnouveau.api.documentation.DocClientUtils;
import com.hollingsworth.arsnouveau.api.documentation.SinglePageCtor;
import com.hollingsworth.arsnouveau.api.documentation.export.DocExporter;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.SpellSchool;
import com.hollingsworth.arsnouveau.api.spell.SpellTier;
import com.hollingsworth.arsnouveau.client.gui.documentation.AugmentIcon;
import com.hollingsworth.arsnouveau.client.gui.documentation.BaseDocScreen;
import com.hollingsworth.nuggets.client.gui.BaseButton;
import com.hollingsworth.nuggets.client.gui.GuiHelpers;
import com.hollingsworth.nuggets.client.gui.NuggetImageButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
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
        setTooltipIfHovered(DocClientUtils.renderItemStack(guiGraphics, x + 3, y + 3, mouseX, mouseY, renderStack));
        GuiHelpers.drawCenteredStringNoShadow(font, guiGraphics, title, x + 70, y + 7, 0);
        DocClientUtils.blit(guiGraphics, DocAssets.GLYPH_DETAILS, x, y + 23);
        return 39;
    }

    @Override
    public List<AbstractWidget> getExtras() {
        List<AbstractWidget> extras = super.getExtras();
        List<SpellSchool> schoolList = spellPart.spellSchools;
        SpellSchool firstSchool = schoolList.isEmpty() ? null : schoolList.getFirst();
        DocAssets.BlitInfo schoolImage =  firstSchool == null ? DocAssets.NA_ICON : firstSchool.getIcon();
        BaseButton schoolButton = new NuggetImageButton(x + 2, y + 25, schoolImage.width(), schoolImage.height(), schoolImage.location(), (b) -> {}).setPlaySound(false);
        if(firstSchool != null) {
            schoolButton.withTooltip(firstSchool.getTextComponent());
        }
        extras.add(schoolButton);

        DocAssets.BlitInfo type = spellPart.getTypeIcon();
        extras.add(new NuggetImageButton(x + 15, y + 25, type.width(), type.height(), type.location(), (b) -> {}).setPlaySound(false).withTooltip(spellPart.getTypeName()));

        SpellTier tier = spellPart.getConfigTier();
        DocAssets.BlitInfo tierAsset = tier.docInfo.get();
        extras.add(new NuggetImageButton(x + 26, y + 25, tierAsset.width(), tierAsset.height(), tierAsset.location(), (b) -> {})
                .setPlaySound(false)
                .withTooltip(Component.translatable(tier.id.getNamespace() + ".tier." + tier.id.getPath())));

        int augmentCount = 0;
        for(AbstractSpellPart spellPart1 : spellPart.compatibleAugments){
            extras.add(new AugmentIcon(spellPart, x + 33 + (augmentCount * 11), y + 22, 10, 10, Component.empty(), (b) -> {},  spellPart1.glyphItem.getDefaultInstance(), parent)
                    .withScale(10).setPlaySound(false));
            augmentCount++;
        }
        return extras;
    }

    @Override
    public void addExportProperties(JsonObject object) {
        super.addExportProperties(object);
        if(this.spellPart != null){
            object.addProperty(DocExporter.TIER_PROPERTY, this.spellPart.getConfigTier().value);
        }
    }
}
