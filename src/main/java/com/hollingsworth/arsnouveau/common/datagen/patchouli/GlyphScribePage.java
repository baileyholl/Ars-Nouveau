package com.hollingsworth.arsnouveau.common.datagen.patchouli;

import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import net.minecraft.resources.ResourceLocation;

public class GlyphScribePage extends AbstractPage {

    public GlyphScribePage(AbstractSpellPart spellPart) {
        object.addProperty("recipe", spellPart.getRegistryName().toString());
    }

    @Override
    public ResourceLocation getType() {
        return new ResourceLocation("ars_nouveau:glyph_recipe");
    }
}
