package com.hollingsworth.craftedmagic.spell;

import com.hollingsworth.craftedmagic.api.AbstractSpellPart;
import com.hollingsworth.craftedmagic.spell.method.MethodType;
import com.hollingsworth.craftedmagic.spell.effect.EffectType;

public class SpellResolver {
    MethodType castType;
    EffectType effectType;

    public SpellResolver(MethodType cast, EffectType spell){
        this.castType = cast;
        this.effectType = spell;
    }

    public SpellResolver(AbstractSpellPart cast, AbstractSpellPart spell){
        this((MethodType) cast, (EffectType) spell);
    }

    public SpellResolver(String castTag, String spellTag){
        this(SpellManager.spellManager.spellList.get(castTag), SpellManager.spellManager.spellList.get(spellTag));
    }


    public void onCast(){
        castType.onCast();
        effectType.onCast();
    }
}
