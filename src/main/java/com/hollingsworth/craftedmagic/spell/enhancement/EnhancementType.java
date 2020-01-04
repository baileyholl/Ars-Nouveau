package com.hollingsworth.craftedmagic.spell.enhancement;

import com.hollingsworth.craftedmagic.api.AbstractSpellPart;

public class EnhancementType extends AbstractSpellPart {
    public EnhancementType(String tag) {
        super(tag);
    }

    @Override
    public int getManaCost() {
        return 0;
    }
}
