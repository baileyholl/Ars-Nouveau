package com.hollingsworth.arsnouveau.api.item.inv;

import com.hollingsworth.arsnouveau.common.items.ItemScroll;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

public class StorageItemHandler extends FilterableItemHandler {

    public StorageItemHandler(IItemHandler handler, FilterSet filters, SlotCache slotCache) {
        super(handler, filters);
        withSlotCache(slotCache);
    }

    @Override
    public InteractResult canExtract(ItemStack stack) {
        ItemScroll.SortPref pref = getHighestPreference(stack);
        return new InteractResult(pref, true);
    }
}
