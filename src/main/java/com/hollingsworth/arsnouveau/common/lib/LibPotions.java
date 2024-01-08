package com.hollingsworth.arsnouveau.common.lib;

public class LibPotions {

    public static final String SHOCKED = "shocked";
    public static final String HEX = "hex";
    public static final String MAGIC_FIND = "magic_find";
    public static final String BOUNCE = "bounce";
    public static final String GRAVITY = "gravity";
    public static final String SNARE = "snared";
    public static final String GLIDE = "glide";
    public static final String SCRYING = "scrying";
    public static final String FLIGHT = "flight";
    public static final String FLARE = "flare";

    public static final String MANA_REGEN = "mana_regen";
    public static final String SPELL_DAMAGE = "spell_damage";

    public static final String FAMILIAR_SICKNESS = "familiar_sickness";
    public static final String SUMMONING_SICKNESS = "summoning_sickness";
    public static final String RECOVERY = "recovery";
    public static final String BLAST = "blasting";
    public static final String FREEZING = "freezing";
    public static final String DEFENCE = "shielding";

    public static String potion(String base) {
        return base + "_potion";
    }

    public static String longPotion(String base) {
        return potion(base) + "_long";
    }

    public static String strongPotion(String base) {
        return potion(base) + "_strong";
    }


}
