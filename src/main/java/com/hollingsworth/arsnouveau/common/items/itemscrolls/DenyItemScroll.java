package com.hollingsworth.arsnouveau.common.items.itemscrolls;

import com.hollingsworth.arsnouveau.common.items.ItemScroll;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

public class DenyItemScroll extends ItemScroll {

    public DenyItemScroll() {
        super();
    }

    public DenyItemScroll(Properties properties) {
        super(properties);
    }

    @Override
    public SortPref getSortPref(ItemStack stackToStore, ItemStack scrollStack, IItemHandler inventory) {
        ItemScrollData data = new ItemScrollData(scrollStack);
        return !data.containsStack(stackToStore) ? SortPref.HIGH : SortPref.INVALID;
    }
}
