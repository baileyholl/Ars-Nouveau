package com.hollingsworth.arsnouveau.common.spell.augment;

import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import javax.annotation.Nullable;

public class AugmentFortune extends AbstractAugment {
    public static AugmentFortune INSTANCE = new AugmentFortune();

    private AugmentFortune() {
        super(GlyphLib.AugmentFortuneID, "Fortune");
    }

    @Override
    public int getDefaultManaCost() {
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
