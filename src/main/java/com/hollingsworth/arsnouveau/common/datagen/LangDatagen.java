package com.hollingsworth.arsnouveau.common.datagen;

import com.hollingsworth.arsnouveau.common.items.Glyph;
import com.hollingsworth.arsnouveau.common.items.ItemsRegistry;
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
                add(i, "Glyph:" + ((Glyph) i).spellPart.name);
            }
        });
    }
}
