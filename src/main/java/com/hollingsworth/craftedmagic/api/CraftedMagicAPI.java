package com.hollingsworth.craftedmagic.api;

import com.hollingsworth.craftedmagic.ModConfig;
import com.hollingsworth.craftedmagic.spell.effect.EffectDig;
import com.hollingsworth.craftedmagic.spell.method.ModifierProjectile;

import java.util.HashMap;

public class CraftedMagicAPI {
    private static CraftedMagicAPI craftedMagicAPI = null;

    public HashMap<String,AbstractSpellPart> spell_map;



    private CraftedMagicAPI(){
        spell_map = new HashMap<>();
        spell_map.put(ModConfig.ModifierProjectileID, new ModifierProjectile());
        spell_map.put(ModConfig.EffectDigID, new EffectDig());
    }

    public static CraftedMagicAPI getInstance(){
        if(craftedMagicAPI == null)
            craftedMagicAPI = new CraftedMagicAPI();
        return craftedMagicAPI;
    }
}
