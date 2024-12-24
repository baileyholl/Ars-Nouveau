package com.hollingsworth.arsnouveau.api.spell;

import com.hollingsworth.arsnouveau.api.documentation.DocAssets;

public class SpellSchools {

    public static SpellSchool ABJURATION = new SpellSchool("abjuration", DocAssets.ALCHEMANCY_ICON);
    public static SpellSchool CONJURATION = new SpellSchool("conjuration", DocAssets.CONJURATION_ICON);
    public static SpellSchool NECROMANCY = new SpellSchool("necromancy");
    public static SpellSchool MANIPULATION = new SpellSchool("manipulation", DocAssets.MANIPULATION_ICON);
    public static SpellSchool ELEMENTAL_AIR = new SpellSchool("air", DocAssets.AIR_ICON);
    public static SpellSchool ELEMENTAL_EARTH = new SpellSchool("earth", DocAssets.EARTH_ICON);
    public static SpellSchool ELEMENTAL_FIRE = new SpellSchool("fire", DocAssets.FIRE_ICON);
    public static SpellSchool ELEMENTAL_WATER = new SpellSchool("water", DocAssets.WATER_ICON);
    public static SpellSchool ELEMENTAL = new SpellSchool("elemental").withSubSchool(ELEMENTAL_AIR).withSubSchool(ELEMENTAL_EARTH).withSubSchool(ELEMENTAL_FIRE).withSubSchool(ELEMENTAL_WATER);
}
