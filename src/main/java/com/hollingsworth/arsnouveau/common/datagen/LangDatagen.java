package com.hollingsworth.arsnouveau.common.datagen;

import com.hollingsworth.arsnouveau.common.items.FamiliarScript;
import com.hollingsworth.arsnouveau.common.items.Glyph;
import com.hollingsworth.arsnouveau.common.items.RitualTablet;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;

public class LangDatagen extends LanguageProvider {
    public LangDatagen(DataGenerator gen, String modid, String locale) {
        super(gen, modid, locale);
    }

    @Override
    protected void addTranslations() {
        ItemsRegistry.RegistrationHandler.ITEMS.forEach(i ->{
            if(i instanceof Glyph){
                add(i, "Glyph of " + ((Glyph) i).spellPart.name);
                add("ars_nouveau.glyph_desc." + ((Glyph) i).spellPart.tag, ((Glyph) i).spellPart.getBookDescription());
                add("ars_nouveau.glyph_name." + ((Glyph) i).spellPart.tag, ((Glyph) i).spellPart.getName());
            }
            if(i instanceof RitualTablet){
                System.out.println(i);
                add(i, "Tablet of " +((RitualTablet) i).ritual.getLangName());
                add( "ars_nouveau.ritual_desc." + ((RitualTablet) i).ritual.getID(),((RitualTablet) i).ritual.getLangDescription());
            }

            if(i instanceof FamiliarScript){
                add(i, "Bound Script: " + ((FamiliarScript) i).familiar.getBookName());
                add("ars_nouveau.familiar_desc." + ((FamiliarScript) i).familiar.id, ((FamiliarScript) i).familiar.getBookDescription());
                add("ars_nouveau.familiar_name." + ((FamiliarScript) i).familiar.id, ((FamiliarScript) i).familiar.getBookName());
            }
        });
    }
}
