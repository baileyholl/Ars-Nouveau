package com.hollingsworth.arsnouveau.common.spell.augment;

import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.hollingsworth.arsnouveau.api.spell.SpellTier;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;

public class AugmentAccelerate extends AbstractAugment {

    public static AugmentAccelerate INSTANCE = new AugmentAccelerate();

    private AugmentAccelerate() {
        super(GlyphLib.AugmentAccelerateID, "Accelerate");
    }

    @Override
    public SpellStats.Builder applyModifiers(SpellStats.Builder builder, AbstractSpellPart spellPart) {
        builder.addAccelerationModifier(1.0F);
        return super.applyModifiers(builder, spellPart);
    }

    @Override
    public int getDefaultManaCost() {
        return 10;
    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.TWO;
    }

    @Override
    public String getBookDescription() {
        return "Increases the speed of projectile spells.";
    }
}
