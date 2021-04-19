package com.hollingsworth.arsnouveau.common.spell.augment;

import com.hollingsworth.arsnouveau.GlyphLib;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

import javax.annotation.Nullable;

import com.hollingsworth.arsnouveau.api.spell.ISpellTier.Tier;

public class AugmentAccelerate extends AbstractAugment {

    public AugmentAccelerate() {
        super(GlyphLib.AugmentAccelerateID, "Accelerate");
    }

    @Override
    public int getManaCost() {
        return 10;
    }

    @Nullable
    @Override
    public Item getCraftingReagent() {
        return Items.POWERED_RAIL;
    }

    @Override
    public Tier getTier() {
        return Tier.TWO;
    }

    @Override
    public String getBookDescription() {
        return "Increases the speed of projectile spells.";
    }
}
