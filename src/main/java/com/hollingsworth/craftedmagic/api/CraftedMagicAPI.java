package com.hollingsworth.craftedmagic.api;

import com.hollingsworth.craftedmagic.ModConfig;
import com.hollingsworth.craftedmagic.spell.effect.*;
import com.hollingsworth.craftedmagic.spell.method.MethodSelf;
import com.hollingsworth.craftedmagic.spell.method.MethodTouch;
import com.hollingsworth.craftedmagic.spell.method.MethodProjectile;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;

public class CraftedMagicAPI {
    private static CraftedMagicAPI craftedMagicAPI = null;

    public HashMap<String,AbstractSpellPart> spell_map;



    private CraftedMagicAPI(){
        spell_map = new HashMap<>();
        spell_map.put(ModConfig.MethodProjectileID, new MethodProjectile());
        spell_map.put(ModConfig.MethodTouchID, new MethodTouch());
        spell_map.put(ModConfig.MethodSelfID, new MethodSelf());

        spell_map.put(ModConfig.EffectDigID, new EffectDig());
        spell_map.put(ModConfig.EffectDamageID, new EffectDamage());
        spell_map.put(ModConfig.EffectIgniteID, new EffectIgnite());
        spell_map.put(ModConfig.EffectPhantomBlockID, new EffectPhantomBlock());
        spell_map.put(ModConfig.EffectHealID, new EffectHeal());
        spell_map.put(ModConfig.EffectGrowID, new EffectGrow());
        spell_map.put(ModConfig.EffectKnockbackID, new EffectKnockback());

    }

    public static CraftedMagicAPI getInstance(){
        if(craftedMagicAPI == null)
            craftedMagicAPI = new CraftedMagicAPI();
        return craftedMagicAPI;
    }
}
