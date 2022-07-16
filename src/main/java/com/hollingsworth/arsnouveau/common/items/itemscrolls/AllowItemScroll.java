package com.hollingsworth.arsnouveau.common.items.itemscrolls;

import com.hollingsworth.arsnouveau.common.items.ItemScroll;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import com.hollingsworth.arsnouveau.common.items.ItemScroll.ItemScrollData;
import com.hollingsworth.arsnouveau.common.items.ItemScroll.SortPref;
import net.minecraft.world.item.Item.Properties;

public class AllowItemScroll extends ItemScroll {

    public AllowItemScroll() {
        super();
    }

    public AllowItemScroll(Properties properties) {
        super(properties);
    }

    @Override
    public SortPref getSortPref(ItemStack stackToStore, ItemStack scrollStack, IItemHandler inventory) {
        ItemScrollData data = new ItemScrollData(scrollStack);
        return data.containsStack(stackToStore) ? SortPref.HIGH : SortPref.INVALID;
    }
}
