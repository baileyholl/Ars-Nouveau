package com.hollingsworth.arsnouveau.api;

import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;

import java.util.List;

public class RegistryHelper {
    /**
     * Helper method for generating a folder of configs for a given spell. This conforms to the AN config spec and is highly recommended.
     */
    public static void generateConfig(String modID, List<AbstractSpellPart> glyphs){
        FMLPaths.getOrCreateGameRelativePath(FMLPaths.CONFIGDIR.get().resolve(modID), modID);
        for(AbstractSpellPart spellPart : glyphs){
            ForgeConfigSpec spec;
            ForgeConfigSpec.Builder spellBuilder = new ForgeConfigSpec.Builder();
            spellPart.buildConfig(spellBuilder);
            spec = spellBuilder.build();
            spellPart.CONFIG = spec;
            ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, spellPart.CONFIG, modID + "/" + spellPart.getId() +".toml");
        }
    }
}
