package com.hollingsworth.arsnouveau.common.spell.augment;

import com.hollingsworth.arsnouveau.GlyphLib;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import net.minecraft.item.Item;

public class AugmentDurationDown extends AbstractAugment {
    public static AugmentDurationDown INSTANCE = new AugmentDurationDown();

    private AugmentDurationDown() {
        super(GlyphLib.AugmentDurationDown, "Duration Down");
    }

    @Override
    public Tier getTier() {
        return Tier.TWO;
    }


    @Override
    public Item getCraftingReagent() {
        return ArsNouveauAPI.getInstance().getGlyphItem(AugmentExtendTime.INSTANCE);
    }

    @Override
    public int getManaCost() {
        return 15;
    }

    @Override
    public SpellStats.Builder applyModifiers(SpellStats.Builder builder, AbstractSpellPart spellPart) {
        builder.addDurationModifier(-1.0d);
        return super.applyModifiers(builder, spellPart);
    }

    @Override
    public String getBookDescription() {
        return "Reduces the duration of spells like potion effects, delay, redstone signal, and others.";
    }
}
