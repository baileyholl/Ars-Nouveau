package com.hollingsworth.arsnouveau.common.items.itemscrolls;

import com.hollingsworth.arsnouveau.common.items.ItemScroll;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.items.IItemHandler;

import com.hollingsworth.arsnouveau.common.items.ItemScroll.SortPref;
import net.minecraft.world.item.Item.Properties;

public class AllowItemScroll extends ItemScroll {

    public AllowItemScroll(String reg) {
        super(reg);
    }

    public AllowItemScroll(Properties properties, String reg) {
        super(properties, reg);
    }

    @Override
    public SortPref getSortPref(ItemStack stackToStore, CompoundTag scrollTag, IItemHandler inventory) {
        return !ItemScroll.containsItem(stackToStore, scrollTag) ? SortPref.INVALID : SortPref.HIGH;
    }
}
