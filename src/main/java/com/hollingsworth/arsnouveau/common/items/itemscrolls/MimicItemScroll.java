package com.hollingsworth.arsnouveau.common.items.itemscrolls;

import com.hollingsworth.arsnouveau.common.items.ItemScroll;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public class MimicItemScroll extends ItemScroll {

    public MimicItemScroll(String reg) {
        super(reg);
    }

    public MimicItemScroll(Properties properties, String reg) {
        super(properties, reg);
    }

    @Override
    public SortPref getSortPref(ItemStack stackToStore, CompoundTag scrollTag, IItemHandler inventory) {
        for(int i = 0; i < inventory.getSlots(); i++){
            if(inventory.getStackInSlot(i).sameItemStackIgnoreDurability(stackToStore))
                return SortPref.HIGHEST;
        }
        return SortPref.INVALID;
    }
}
