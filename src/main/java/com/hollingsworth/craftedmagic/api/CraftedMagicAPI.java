package com.hollingsworth.craftedmagic.api;

import com.hollingsworth.craftedmagic.ModConfig;
import com.hollingsworth.craftedmagic.spell.augment.AugmentAOE;
import com.hollingsworth.craftedmagic.spell.augment.AugmentEmpower;
import com.hollingsworth.craftedmagic.spell.augment.AugmentExtendTime;
import com.hollingsworth.craftedmagic.spell.augment.AugmentPierce;
import com.hollingsworth.craftedmagic.spell.effect.*;
import com.hollingsworth.craftedmagic.spell.method.MethodSelf;
import com.hollingsworth.craftedmagic.spell.method.MethodTouch;
import com.hollingsworth.craftedmagic.spell.method.MethodProjectile;

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
        spell_map.put(ModConfig.EffectAccelerateID, new EffectAccelerate());
        spell_map.put(ModConfig.EffectLightID, new EffectLight());
        spell_map.put(ModConfig.EffectDispelID, new EffectDispel());
        spell_map.put(ModConfig.EffectFreezeID, new EffectFreeze());
        spell_map.put(ModConfig.EffectPullID, new EffectPull());
        spell_map.put(ModConfig.EffectBlinkID, new EffectBlink());
        spell_map.put(ModConfig.EffectJumpID, new EffectJump());
        spell_map.put(ModConfig.EffectExplosion, new EffectExplosion());


        spell_map.put(ModConfig.AugmentEmpower, new AugmentEmpower());
        spell_map.put(ModConfig.AugmentAOEID, new AugmentAOE());
        spell_map.put(ModConfig.AugmentExtendTimeID, new AugmentExtendTime());
        spell_map.put(ModConfig.AugmentPierceID, new AugmentPierce());


    }

    public static CraftedMagicAPI getInstance(){
        if(craftedMagicAPI == null)
            craftedMagicAPI = new CraftedMagicAPI();
        return craftedMagicAPI;
    }
}
