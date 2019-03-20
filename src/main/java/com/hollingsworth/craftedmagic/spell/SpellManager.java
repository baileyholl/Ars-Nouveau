package com.hollingsworth.craftedmagic.spell;

import com.hollingsworth.craftedmagic.api.AbstractSpellPart;
import com.hollingsworth.craftedmagic.spell.method.ModifierProjectile;
import com.hollingsworth.craftedmagic.spell.effect.EffectDig;

import java.util.HashMap;

public final class SpellManager {
    public static SpellManager spellManager = new SpellManager();

    public HashMap<String, AbstractSpellPart> spellList = new HashMap<>();

    private SpellManager(){
        spellList.put("Dig", new EffectDig());
        spellList.put("Projectile", new ModifierProjectile());
    }
}
