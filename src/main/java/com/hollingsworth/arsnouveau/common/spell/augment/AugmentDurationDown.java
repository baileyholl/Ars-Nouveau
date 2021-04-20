package com.hollingsworth.arsnouveau.common.spell.augment;

import com.hollingsworth.arsnouveau.GlyphLib;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import net.minecraft.item.Item;

import com.hollingsworth.arsnouveau.api.spell.ISpellTier.Tier;

public class AugmentDurationDown extends AbstractAugment {

    public AugmentDurationDown() {
        super(GlyphLib.AugmentDurationDown, "Duration Down");
    }

    @Override
    public Tier getTier() {
        return Tier.TWO;
    }


    @Override
    public Item getCraftingReagent() {
        return ArsNouveauAPI.getInstance().getGlyphItem(new AugmentExtendTime());
    }

    @Override
    public int getManaCost() {
        return 15;
    }

    @Override
    public String getBookDescription() {
        return "Reduces the duration of spells like potion effects, delay, redstone signal, and others.";
    }
}
