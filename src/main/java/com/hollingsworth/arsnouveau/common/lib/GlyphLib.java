package com.hollingsworth.arsnouveau.common.lib;

public class GlyphLib {
    public static final String MethodProjectileID = prependGlyph("projectile");
    public static final String MethodTouchID = prependGlyph("touch");
    public static final String MethodSelfID = prependGlyph("self");
    public static final String MethodPantomimeID = prependGlyph("pantomime");
    public static final String EffectRuneID = prependGlyph("rune");

    public static final String EffectBreakID = prependGlyph("break");
    public static final String EffectHarmID = prependGlyph("harm");
    public static final String EffectIgniteID = prependGlyph("ignite");
    public static final String EffectPhantomBlockID = prependGlyph("phantom_block");
    public static final String EffectGrowID = prependGlyph("grow");
    public static final String EffectHealID = prependGlyph("heal");
    public static final String EffectKnockbackID = prependGlyph("gust");
    public static final String EffectLightID = prependGlyph("light");
    public static final String EffectDispelID = prependGlyph("dispel");
    public static final String EffectFreezeID = prependGlyph("freeze");
    public static final String EffectLaunchID = prependGlyph("launch");
    public static final String EffectPullID = prependGlyph("pull");
    public static final String EffectBlinkID = prependGlyph("blink");
    public static final String EffectBubbleID = prependGlyph("bubble");
    public static final String EffectNameID = prependGlyph("name");

    public static final String EffectAnimateID = prependGlyph("animate_block");
    public static final String EffectExplosionID = prependGlyph("explosion");
    public static final String EffectLightningID = prependGlyph("lightning");
    public static final String EffectSlowfallID = prependGlyph("slowfall");
    public static final String EffectFangsID = prependGlyph("fangs");
    public static final String EffectSummonVexID = prependGlyph("summon_vex");
    public static final String EffectHarvestID = prependGlyph("harvest");
    public static final String EffectLeapID = prependGlyph("leap");
    public static final String AugmentAccelerateID = prependGlyph("accelerate");
    public static final String AugmentDecelerateID = prependGlyph("decelerate");
    public static final String AugmentExtendTimeID = prependGlyph("extend_time");
    public static final String AugmentReduceTime = prependGlyph("duration_down"); //TODO: 1.22 change key to reduce_time
    public static final String AugmentSensitiveID = prependGlyph("sensitive");

    public static final String AugmentRandomizeID = prependGlyph("randomize");
    public static final String AugmentPierceID = prependGlyph("pierce");
    public static final String AugmentAOEID = prependGlyph("aoe");
    public static final String AugmentAmplifyID = prependGlyph("amplify");
    public static final String AugmentDampenID = prependGlyph("dampen");
    public static final String AugmentExtractID = prependGlyph("extract");
    public static final String AugmentFortuneID = prependGlyph("fortune");
    public static final String AugmentSplitID = prependGlyph("split");
    public static final String EffectSnareID = prependGlyph("snare");
    public static final String EffectSmeltID = prependGlyph("smelt");
    public static final String EffectEnderChestID = prependGlyph("ender_inventory");
    public static final String EffectPickupID = prependGlyph("pickup");
    public static final String EffectInteractID = prependGlyph("interact");
    public static final String EffectPlaceBlockID = prependGlyph("place_block");
    public static final String EffectDelayID = prependGlyph("delay");
    public static final String EffectRedstoneID = prependGlyph("redstone_signal");
    public static final String EffectIntangibleID = prependGlyph("intangible");
    public static final String EffectFellID = prependGlyph("fell");
    public static final String EffectInvisibilityID = prependGlyph("invisibility");
    public static final String EffectWitherID = prependGlyph("wither");
    public static final String EffectExchangeID = prependGlyph("exchange");
    public static final String EffectCraftID = prependGlyph("craft");
    public static final String EffectColdSnapID = prependGlyph("cold_snap");
    public static final String EffectFlareID = prependGlyph("flare");
    public static final String EffectGravityID = prependGlyph("gravity");
    public static final String EffectConjureWaterID = prependGlyph("conjure_water");
    public static final String EffectCutID = prependGlyph("cut");
    public static final String EffectCrushID = prependGlyph("crush");
    public static final String EffectSummonWolvesID = prependGlyph("summon_wolves");
    public static final String EffectSummonUndeadID = prependGlyph("summon_undead");

    public static final String EffectSummonSteedID = prependGlyph("summon_steed");
    public static final String EffectDecoyID = prependGlyph("summon_decoy");
    public static final String EffectHexID = prependGlyph("hex");
    public static final String MethodUnderfootID = prependGlyph("underfoot");
    public static final String EffectGlideID = prependGlyph("glide");
    public static final String EffectOrbitID = prependGlyph("orbit");
    public static final String EffectFireworkID = prependGlyph("firework");
    public static final String EffectTossID = prependGlyph("toss");
    public static final String EffectWindshearID = prependGlyph("wind_shear");
    public static final String EffectEarthshakeID = prependGlyph("earthshake");
    public static final String EffectBounceID = prependGlyph("bounce");
    public static final String EffectEvaporate = prependGlyph("evaporate");

    public static final String EffectLingerID = prependGlyph("linger");
    public static final String EffectSenseMagicID = prependGlyph("sense_magic");
    public static final String EffectInfuseID = prependGlyph("infuse");

    public static final String EffectRotateID = prependGlyph("rotate");
    public static final String EffectWallId = prependGlyph("wall");

    public static final String EffectBurstID = prependGlyph("burst");


    public static String prependGlyph(String glyph) {
        return "glyph_" + glyph;
    }
}
