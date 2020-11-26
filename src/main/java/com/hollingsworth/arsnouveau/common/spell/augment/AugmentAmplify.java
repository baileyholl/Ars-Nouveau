package com.hollingsworth.arsnouveau.common.spell.augment;

import com.hollingsworth.arsnouveau.ModConfig;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

import javax.annotation.Nullable;

public class AugmentAmplify extends AbstractAugment {
    public AugmentAmplify() {
        super(ModConfig.AugmentAmplifyID, "Amplify");
    }

    @Override
    public int getManaCost() {
        return 20;
    }

    @Nullable
    @Override
    public Item getCraftingReagent() {
        return Items.DIAMOND;
    }

    @Override
    public Tier getTier() {
        return Tier.ONE;
    }

    @Override
    protected String getBookDescription() {
        return "Additively increases the power of most spell effects. Can empower Break (requires 2 amplifies) to harvest Obsidian, and increases the damage of spells.";
    }
}
