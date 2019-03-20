package com.hollingsworth.craftedmagic.api;

public abstract class AbstractSpellPart {

    public abstract int getManaCost();
    /*Tag for NBT data and SpellManager#spellList*/
    public abstract String getTag();

    public abstract void onCast();
}
