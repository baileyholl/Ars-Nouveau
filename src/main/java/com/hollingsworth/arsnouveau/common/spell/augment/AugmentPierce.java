package com.hollingsworth.arsnouveau.common.spell.augment;

import com.hollingsworth.arsnouveau.ModConfig;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

import javax.annotation.Nullable;

public class AugmentPierce extends AbstractAugment {
    public AugmentPierce() {
        super(ModConfig.AugmentPierceID, "Pierce");
    }

    @Override
    public int getManaCost() {
        return 20;
    }

    @Override
    public Tier getTier() {
        return Tier.TWO;
    }

    @Nullable
    @Override
    public Item getCraftingReagent() {
        return Items.ARROW;
    }

    @Override
    protected String getBookDescription() {
        return "When applied to the Projectile spell, projectiles may continue through their path an additional time after hitting a mob or block.";
    }
}
