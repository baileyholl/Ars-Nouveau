package com.hollingsworth.arsnouveau.common.spell.augment;

import com.hollingsworth.arsnouveau.GlyphLib;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import javax.annotation.Nullable;

public class AugmentSplit extends AbstractAugment {
    public static AugmentSplit INSTANCE = new AugmentSplit();

    private AugmentSplit() {
        super(GlyphLib.AugmentSplitID, "Split");
    }

    @Override
    public int getDefaultManaCost() {
        return 20;
    }

    @Override
    public Tier getTier() {
        return  Tier.THREE;
    }

    @Nullable
    @Override
    public Item getCraftingReagent() {
        return Items.STONECUTTER;
    }

    @Override
    public String getBookDescription() {
        return "Causes multiple projectiles to be cast at once. Each projectile applies a set of effects.";
    }
}
