package com.hollingsworth.craftedmagic.spell.enhancement;

import com.hollingsworth.craftedmagic.api.AbstractSpellPart;

public class EnhancementType extends AbstractSpellPart {
    public EnhancementType(String tag, String description) {
        super(tag, description);
    }

    @Override
    public int getManaCost() {
        return 0;
    }
}
