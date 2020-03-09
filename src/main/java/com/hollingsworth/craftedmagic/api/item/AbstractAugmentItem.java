package com.hollingsworth.craftedmagic.api.item;

import com.hollingsworth.craftedmagic.api.ISpellBonus;
import com.hollingsworth.craftedmagic.api.spell.AbstractAugment;
import net.minecraft.item.Item;

public abstract class AbstractAugmentItem extends Item implements ISpellBonus {
    public AbstractAugmentItem(Properties properties) {
        super(properties);
    }
}
