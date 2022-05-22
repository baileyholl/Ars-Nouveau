package com.hollingsworth.arsnouveau.api;

import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;

import java.util.List;

public class RegistryHelper {
    /**
     * Helper method for generating a folder of configs for a given spell. This conforms to the AN config spec and is highly recommended.
     */
    @Deprecated
    public static void generateConfig(String modID, List<AbstractSpellPart> glyphs){
        //no use for this function anymore, but it's not safe to remove it as other addons might use it.
    }
}
