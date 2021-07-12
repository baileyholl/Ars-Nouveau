package com.hollingsworth.arsnouveau.common.items.itemscrolls;

import com.hollingsworth.arsnouveau.common.items.ItemScroll;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.items.IItemHandler;

public class DenyItemScroll extends ItemScroll {

    public DenyItemScroll(String reg) {
        super(reg);
    }

    public DenyItemScroll(Properties properties, String reg) {
        super(properties, reg);
    }

    @Override
    public SortPref getSortPref(ItemStack stackToStore, CompoundNBT scrollTag, IItemHandler inventory) {
        return ItemScroll.containsItem(stackToStore, scrollTag) ? SortPref.INVALID : SortPref.LOW;
    }
}
