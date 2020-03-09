package com.hollingsworth.craftedmagic.items.curios;

import com.hollingsworth.craftedmagic.ArsNouveau;
import com.hollingsworth.craftedmagic.ModConfig;
import com.hollingsworth.craftedmagic.api.ArsNouveauAPI;
import com.hollingsworth.craftedmagic.api.item.AbstractAugmentItem;
import com.hollingsworth.craftedmagic.api.spell.AbstractAugment;
import com.hollingsworth.craftedmagic.spell.augment.AugmentAmplify;
import net.minecraft.item.Item;

public class RingOfAmplify extends AbstractAugmentItem {

    public RingOfAmplify() {
        super(new Item.Properties().maxStackSize(1).group(ArsNouveau.itemGroup));
    }

    @Override
    public AbstractAugment getBonusAugment() {
        return (AbstractAugment) ArsNouveauAPI.getInstance().getSpell_map().get(ModConfig.AugmentAmplifyID);
    }

    @Override
    public int getBonusLevel() {
        return 1;
    }
}
