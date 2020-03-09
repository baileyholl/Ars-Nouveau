package com.hollingsworth.craftedmagic.api;

import com.hollingsworth.craftedmagic.ModConfig;
import com.hollingsworth.craftedmagic.api.spell.AbstractSpellPart;
import com.hollingsworth.craftedmagic.spell.augment.*;
import com.hollingsworth.craftedmagic.spell.effect.*;
import com.hollingsworth.craftedmagic.spell.method.MethodProjectile;
import com.hollingsworth.craftedmagic.spell.method.MethodSelf;
import com.hollingsworth.craftedmagic.spell.method.MethodTouch;

import java.util.HashMap;

public class ArsNouveauAPI {
    private static ArsNouveauAPI arsNouveauAPI = null;

    private HashMap<String, AbstractSpellPart> spell_map;

    public AbstractSpellPart registerSpell(String id, AbstractSpellPart part){
        return spell_map.put(id, part);
    }

    public HashMap<String, AbstractSpellPart> getSpell_map() {
        return spell_map;
    }

    private ArsNouveauAPI(){
        spell_map = new HashMap<>();
        spell_map.put(ModConfig.MethodProjectileID, new MethodProjectile());
        spell_map.put(ModConfig.MethodTouchID, new MethodTouch());
        spell_map.put(ModConfig.MethodSelfID, new MethodSelf());

        spell_map.put(ModConfig.EffectBreakID, new EffectBreak());
        spell_map.put(ModConfig.EffectHarmID, new EffectHarm());
        spell_map.put(ModConfig.EffectIgniteID, new EffectIgnite());
        spell_map.put(ModConfig.EffectPhantomBlockID, new EffectPhantomBlock());
        spell_map.put(ModConfig.EffectHealID, new EffectHeal());
        spell_map.put(ModConfig.EffectGrowID, new EffectGrow());
        spell_map.put(ModConfig.EffectKnockbackID, new EffectKnockback());
        spell_map.put(ModConfig.EffectHasteID, new EffectHaste());
        spell_map.put(ModConfig.EffectLightID, new EffectLight());
        spell_map.put(ModConfig.EffectDispelID, new EffectDispel());
        spell_map.put(ModConfig.EffectFreezeID, new EffectFreeze());
        spell_map.put(ModConfig.EffectLaunchID, new EffectLaunch());
        spell_map.put(ModConfig.EffectPullID, new EffectPull());
        spell_map.put(ModConfig.EffectBlinkID, new EffectBlink());
//        spell_map.put(ModConfig.EffectJumpID, new EffectJump());
        spell_map.put(ModConfig.EffectExplosionID, new EffectExplosion());
        spell_map.put(ModConfig.EffectLightningID, new EffectLightning());
        spell_map.put(ModConfig.EffectSlowfallID, new EffectSlowfall());
        spell_map.put(ModConfig.EffectShieldID, new EffectShield());
        spell_map.put(ModConfig.EffectAquatic, new EffectAquatic());
        spell_map.put(ModConfig.EffectFangsID, new EffectFangs());
        spell_map.put(ModConfig.EffectSummonVexID, new EffectSummonVex());

        spell_map.put(ModConfig.AugmentAccelerateID, new AugmentAccelerate());
        spell_map.put(ModConfig.AugmentSplitID, new AugmentSplit());
        spell_map.put(ModConfig.AugmentAmplifyID, new AugmentAmplify());
        spell_map.put(ModConfig.AugmentAOEID, new AugmentAOE());
        spell_map.put(ModConfig.AugmentExtendTimeID, new AugmentExtendTime());
        spell_map.put(ModConfig.AugmentPierceID, new AugmentPierce());
        spell_map.put(ModConfig.AugmentDampenID, new AugmentDampen());
        spell_map.put(ModConfig.AugmentExtractID, new AugmentExtract());
        spell_map.put(ModConfig.AugmentFortuneID, new AugmentFortune());
    }

    public static ArsNouveauAPI getInstance(){
        if(arsNouveauAPI == null)
            arsNouveauAPI = new ArsNouveauAPI();
        return arsNouveauAPI;
    }


}
