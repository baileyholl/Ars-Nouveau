package com.hollingsworth.craftedmagic.spell.cast_types;

import com.hollingsworth.craftedmagic.spell.SpellComponent;

public class ModifierProjectile extends CastingType{

    @Override
    public void onRightClick() {
        System.out.println("Summoning projectile");
    }

    @Override
    public String getTag() {
        return "Projectile";
    }
}
