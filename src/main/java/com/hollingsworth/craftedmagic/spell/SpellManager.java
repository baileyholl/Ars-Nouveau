package com.hollingsworth.craftedmagic.spell;

import com.hollingsworth.craftedmagic.items.Spell;
import com.hollingsworth.craftedmagic.spell.cast_types.ModifierProjectile;
import com.hollingsworth.craftedmagic.spell.spell_types.SpellDig;

import java.util.HashMap;

public final class SpellManager {
    public static SpellManager spellManager = new SpellManager();

    public HashMap<String, SpellComponent> spellList = new HashMap<>();

    private SpellManager(){
        spellList.put("Dig", new SpellDig());
        spellList.put("Projectile", new ModifierProjectile());
    }
}
