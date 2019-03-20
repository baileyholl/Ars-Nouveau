package com.hollingsworth.craftedmagic.spell.effect;

public class EffectDig extends EffectType {

    @Override
    public int getManaCost() {
        return 0;
    }

    @Override
    public String getTag() {
        return "Dig";
    }

    @Override
    public void onCast() {

    }
}
