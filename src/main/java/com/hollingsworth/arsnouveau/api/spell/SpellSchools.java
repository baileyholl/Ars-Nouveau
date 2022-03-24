package com.hollingsworth.arsnouveau.api.spell;

public class SpellSchools {

    public static SpellSchool ABJURATION = new SpellSchool("abjuration");
    public static SpellSchool CONJURATION = new SpellSchool("conjuration");
    public static SpellSchool NECROMANCY = new SpellSchool("necromancy");
    public static SpellSchool MANIPULATION = new SpellSchool("manipulation");
    public static SpellSchool ELEMENTAL_AIR = new SpellSchool("air");
    public static SpellSchool ELEMENTAL_EARTH = new SpellSchool("earth");
    public static SpellSchool ELEMENTAL_FIRE = new SpellSchool("fire");
    public static SpellSchool ELEMENTAL_WATER = new SpellSchool("water");
    public static SpellSchool ELEMENTAL = new SpellSchool("elemental").withSubSchool(ELEMENTAL_AIR).withSubSchool(ELEMENTAL_EARTH).withSubSchool(ELEMENTAL_FIRE).withSubSchool(ELEMENTAL_WATER);
}
