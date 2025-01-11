package com.hollingsworth.arsnouveau.api.spell;

public class SpellSchools {

    public static final SpellSchool ABJURATION = new SpellSchool("abjuration");
    public static final SpellSchool CONJURATION = new SpellSchool("conjuration");
    public static final SpellSchool NECROMANCY = new SpellSchool("necromancy");
    public static final SpellSchool MANIPULATION = new SpellSchool("manipulation");
    public static final SpellSchool ELEMENTAL_AIR = new SpellSchool("air");
    public static final SpellSchool ELEMENTAL_EARTH = new SpellSchool("earth");
    public static final SpellSchool ELEMENTAL_FIRE = new SpellSchool("fire");
    public static final SpellSchool ELEMENTAL_WATER = new SpellSchool("water");
    public static final SpellSchool ELEMENTAL = new SpellSchool("elemental").withSubSchool(ELEMENTAL_AIR).withSubSchool(ELEMENTAL_EARTH).withSubSchool(ELEMENTAL_FIRE).withSubSchool(ELEMENTAL_WATER);
}
