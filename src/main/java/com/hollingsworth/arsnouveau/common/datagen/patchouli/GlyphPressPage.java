package com.hollingsworth.arsnouveau.common.datagen.patchouli;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import net.minecraft.resources.ResourceLocation;

public class GlyphPressPage extends AbstractPage{

    public GlyphPressPage(AbstractSpellPart spellPart){
        object.addProperty("recipe", ArsNouveau.MODID + ":" + "glyph_" + spellPart.tag);
    }

    @Override
    public ResourceLocation getType() {
        return new ResourceLocation("ars_nouveau:glyph_recipe");
    }
}
