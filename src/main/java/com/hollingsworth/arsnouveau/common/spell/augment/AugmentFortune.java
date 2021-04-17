package com.hollingsworth.arsnouveau.common.spell.augment;

import com.hollingsworth.arsnouveau.GlyphLib;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

import javax.annotation.Nullable;

import com.hollingsworth.arsnouveau.api.spell.ISpellTier.Tier;

public class AugmentFortune extends AbstractAugment {
    public AugmentFortune() {
        super(GlyphLib.AugmentFortuneID, "Fortune");
    }

    @Override
    public int getManaCost() {
        return 80;
    }

    @Override
    public Tier getTier() {
        return Tier.TWO;
    }

    @Nullable
    @Override
    public Item getCraftingReagent() {
        return Items.RABBIT_FOOT;
    }

    @Override
    public String getBookDescription() {
        return "Increases the drop chance from mobs killed by Damage and blocks that are destroyed by Break. Cannot be combined with Extract";
    }
}
