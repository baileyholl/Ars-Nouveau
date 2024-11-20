package com.hollingsworth.arsnouveau.setup.registry;

import com.hollingsworth.arsnouveau.api.documentation.DocCategory;
import com.hollingsworth.arsnouveau.api.documentation.DocEntry;
import com.hollingsworth.arsnouveau.api.documentation.TextEntry;
import com.hollingsworth.arsnouveau.api.registry.DocumentationRegistry;
import com.hollingsworth.arsnouveau.api.registry.GlyphRegistry;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.SpellTier;
import net.minecraft.network.chat.Component;

public class Documentation {

    public static void initOnWorldReload(){

        for(AbstractSpellPart spellPart : GlyphRegistry.getSpellpartMap().values()){
            var entry = new DocEntry(spellPart.getRegistryName(), spellPart.glyphItem.getDefaultInstance(), Component.literal(spellPart.getLocaleName()));
            entry.addPage(TextEntry.create(spellPart.getBookDescLang().getString()));
            DocumentationRegistry.registerEntry(glyphCategory(spellPart.getConfigTier()), entry);
        }
    }

    public static DocCategory glyphCategory(SpellTier tier){
        return switch (tier.value) {
            case 1 -> DocumentationRegistry.GLYPH_TIER_ONE;
            case 2 -> DocumentationRegistry.GLYPH_TIER_TWO;
            case 3, 99 -> DocumentationRegistry.GLYPH_TIER_THREE;
            default -> DocumentationRegistry.GLYPH_TIER_ONE;
        };
    }
}
