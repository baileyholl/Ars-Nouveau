package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;

public class EffectSummonWolves extends AbstractEffect {
    public EffectSummonWolves(String tag, String description) {
        super(tag, description);
    }

    @Override
    public int getManaCost() {
        return 0;
    }
}
