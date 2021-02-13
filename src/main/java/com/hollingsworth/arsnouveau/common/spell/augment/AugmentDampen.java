package com.hollingsworth.arsnouveau.common.spell.augment;

import com.hollingsworth.arsnouveau.GlyphLib;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

import javax.annotation.Nullable;

public class AugmentDampen extends AbstractAugment {

    public AugmentDampen() {
        super(GlyphLib.AugmentDampenID, "Dampen");
    }

    @Override
    public int getManaCost() {
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
}
