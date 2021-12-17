package com.hollingsworth.arsnouveau.common.spell.augment;

import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import javax.annotation.Nullable;

public class AugmentDampen extends AbstractAugment {
    public static AugmentDampen INSTANCE = new AugmentDampen();

    private AugmentDampen() {
        super(GlyphLib.AugmentDampenID, "Dampen");
    }

    @Override
    public int getDefaultManaCost() {
        return -5;
    }

    @Nullable
    @Override
    public Item getCraftingReagent() {
        return Items.NETHER_BRICK;
    }

    @Override
    public Tier getTier() {
        return Tier.TWO;
    }

    @Override
    public String getBookDescription() {
        return "Decreases the power of most spells. Decreases the mana cost slighty, but never below 0.";
    }

    @Override
    public SpellStats.Builder applyModifiers(SpellStats.Builder builder, AbstractSpellPart spellPart) {
        builder.addAmplification(-1.0);
        return super.applyModifiers(builder, spellPart);
    }
}
