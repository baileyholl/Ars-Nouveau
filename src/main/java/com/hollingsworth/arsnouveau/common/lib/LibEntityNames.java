package com.hollingsworth.arsnouveau.common.lib;

public class LibEntityNames {
    public static final String SPELL_PROJ = "spell_proj";
    public static final String SPELL_PROJ_ARC = "arcing_spell_proj";

    public static final String SPELL_PROJ_HOM = "homing_spell_proj";

    public static final String LINGER = "linger";
    public static final String FANGS = "fangs";

    public static final String ALLY_VEX = "ally_vex";

    public static final String FOLLOW_PROJ = "follow_proj";
    public static final String FLYING_ITEM = "flying_item";
    public static final String RITUAL_PROJ = "ritual";
    public static final String SPELL_ARROW = "spell_arrow";
    public static final String SUMMONED_WOLF = "summon_wolf";
    public static final String SUMMONED_HORSE = "summon_horse";
    public static final String SUMMONED_SKELETON = "summon_skeleton";
    public static final String DUMMY = "dummy";
    public static final String AN_LIGHTNING = "an_lightning";
    public static final String LILY = "lily";
    public static final String NOOK = "nook";
    public static final String BUBBLE = "bubble";
    public static final String ENCHANTED_HOOK = "enchanters_hook";

    public static final String ORBIT_PROJECTILE = "orbit";
    public static final String CHIMERA_SPIKE = "spike";

    public static final String STARBUNCLE = "starbuncle";
    public static final String ALAKARKINOS = "alakarkinos";
    public static final String WHIRLISPRIG = "whirlisprig";
    public static final String WIXIE = "wixie";
    public static final String DRYGMY = "drygmy";
    public static final String BOOKWYRM = "bookwyrm";
    public static final String JABBERWOG = "jabberwog";
    public static final String WILDEN_HUNTER = "wilden_hunter";
    public static final String CINDER = "cinder";
    public static final String WILDEN_STALKER = "wilden_stalker";

    public static final String WILDEN_GUARDIAN = "wilden_guardian";

    public static final String WILDEN_CHIMERA = "wilden_boss";
    public static final String AMETHYST_GOLEM = "amethyst_golem";

    public static final String FAMILIAR_STARBUNCLE = appendFamiliar(STARBUNCLE);
    public static final String FAMILIAR_WHIRLISPRIG = appendFamiliar(WHIRLISPRIG);
    public static final String FAMILIAR_WIXIE = appendFamiliar(WIXIE);
    public static final String FAMILIAR_DRYGMY = appendFamiliar(DRYGMY);
    public static final String FAMILIAR_BOOKWYRM = appendFamiliar(BOOKWYRM);
    public static final String FAMILIAR_JABBERWOG = appendFamiliar(JABBERWOG);
    public static final String FAMILIAR_AMETHYST_GOLEM = appendFamiliar(AMETHYST_GOLEM);

    public static final String FLOURISHING_WEALD_WALKER = appendWeald("flourishing");
    public static final String VEXING_WEALD_WALKER = appendWeald("vexing");
    public static final String BLAZING_WEALD_WALKER = appendWeald("blazing");
    public static final String CASCADING_WEALD_WALKER = appendWeald("cascading");
    public static final String SCRYER_CAMERA = "scryer_camera";
    public static final String GIFT_STARBY = "gift_starby";
    public static final String WALL = "wall";
    public static String appendFamiliar(String fam) {
        return "familiar_" + fam;
    }

    public static String appendWeald(String type) {
        return type + "_weald_walker";
    }
}
