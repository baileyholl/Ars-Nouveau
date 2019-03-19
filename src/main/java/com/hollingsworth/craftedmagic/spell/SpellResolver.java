package com.hollingsworth.craftedmagic.spell;

import com.hollingsworth.craftedmagic.spell.cast_types.CastingType;
import com.hollingsworth.craftedmagic.spell.spell_types.SpellType;

public class SpellResolver {
    CastingType castType;
    SpellType spellType;

    public SpellResolver(CastingType cast, SpellType spell){
        this.castType = cast;
        this.spellType = spell;
    }

    public SpellResolver(SpellComponent cast, SpellComponent spell){
        this((CastingType) cast, (SpellType) spell);
    }

    public SpellResolver(String castTag, String spellTag){
        this(SpellManager.spellManager.spellList.get(castTag), SpellManager.spellManager.spellList.get(spellTag));
    }


    public void onRightClick(){
        castType.onRightClick();
        spellType.onRightClick();
    }
}
