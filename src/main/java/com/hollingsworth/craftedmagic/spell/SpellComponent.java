package com.hollingsworth.craftedmagic.spell;

public abstract class SpellComponent {

    public int manaCost;

    public int getManaCost;
    /*Tag for NBT data and SpellManager#spellList*/
    public abstract String getTag();

    public abstract void onRightClick();
}
