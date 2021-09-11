package com.hollingsworth.arsnouveau.common.spell.augment;

import com.hollingsworth.arsnouveau.GlyphLib;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

import javax.annotation.Nullable;

public class AugmentSensitive extends AbstractAugment {
    public static AugmentSensitive INSTANCE = new AugmentSensitive();

    private AugmentSensitive() {
        super(GlyphLib.AugmentSensitiveID, "Sensitive");
    }

    @Nullable
    @Override
    public Item getCraftingReagent() {
        return Items.SCAFFOLDING;
    }

    @Override
    public String getBookDescription() {
        return "Causes forms to target blocks they normally cannot target. Projectile and Orbit will target grass, and Touch will target fluids and air.";
    }

    @Override
    public int getManaCost() {
        return 10;
    }
}
