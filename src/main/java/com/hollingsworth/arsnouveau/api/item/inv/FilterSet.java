package com.hollingsworth.arsnouveau.api.item.inv;

import com.hollingsworth.arsnouveau.common.items.ItemScroll;
import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public abstract class FilterSet {

    /**
     * Returns the highest preference for a given item.
     * Invalid overrules all other preferences, as the user does NOT want that item to be inserted.
     */
    public abstract ItemScroll.SortPref getHighestPreference(ItemStack stack);

    public static FilterSet forPosition(Level level, BlockPos pos) {
        List<Function<ItemStack, ItemScroll.SortPref>> filters = new ArrayList<>();
        IItemHandler inv = level.getCapability(Capabilities.ItemHandler.BLOCK, pos, null);
        if (inv == null) {
            return new FilterSet.ListSet(filters);
        }
        IFiltersetProvider filtersetProvider = level.getCapability(CapabilityRegistry.FILTERSET_CAPABILITY, pos, null);
        if (filtersetProvider != null) {
            return filtersetProvider.getFilterSet();
        }

        for (ItemFrame i : level.getEntitiesOfClass(ItemFrame.class, new AABB(pos).inflate(1))) {
            BlockPos attachedTo = i.blockPosition().relative(i.getDirection().getOpposite());
            if (!attachedTo.equals(pos)) {
                continue;
            }
            ItemStack stackInFrame = i.getItem();
            if (i.getItem().isEmpty() || stackInFrame.isEmpty()) {
                continue;
            }

            if (stackInFrame.getItem() instanceof ItemScroll scrollItem) {
                filters.add(stackToStore -> scrollItem.getSortPref(stackToStore, stackInFrame, inv));
            } else {
                filters.add(stackToStore -> stackToStore.getItem() == stackInFrame.getItem() ? ItemScroll.SortPref.HIGHEST : ItemScroll.SortPref.INVALID);
            }
        }

        return new FilterSet.ListSet(filters);
    }

    public static class ListSet extends FilterSet {
        public List<Function<ItemStack, ItemScroll.SortPref>> filters;

        public ListSet() {
            this.filters = new ArrayList<>();
        }

        public ListSet(List<Function<ItemStack, ItemScroll.SortPref>> filters) {
            super();
            this.filters = filters;
        }

        public boolean addFilterScroll(ItemStack scrollStack, IItemHandler itemHandler) {
            if (scrollStack.getItem() instanceof ItemScroll itemScroll) {
                return filters.add(stackIn -> itemScroll.getSortPref(stackIn, scrollStack, itemHandler));
            }
            return false;
        }

        /**
         * Returns the highest preference from a list of predicates, unless it is invalid.
         * Invalid overrules all other preferences, as the user does NOT want that item to be inserted.
         */
        public ItemScroll.SortPref getHighestPreference(ItemStack stack) {
            ItemScroll.SortPref pref = ItemScroll.SortPref.LOW;
            for (Function<ItemStack, ItemScroll.SortPref> filter : filters) {
                ItemScroll.SortPref newPref = filter.apply(stack);
                if (newPref == ItemScroll.SortPref.INVALID) {
                    return ItemScroll.SortPref.INVALID;
                } else if (newPref.ordinal() > pref.ordinal()) {
                    pref = newPref;
                }
            }
            return pref;
        }
    }

    public static class Composite extends FilterSet {
        public List<FilterSet> filterSets;

        public Composite(List<FilterSet> filterSets) {
            this.filterSets = filterSets;
        }

        public Composite() {
            this.filterSets = new ArrayList<>();
        }

        public Composite withFilter(FilterSet filterSet) {
            this.filterSets.add(filterSet);
            return this;
        }

        /**
         * Returns the highest preference from a list of predicates, unless it is invalid.
         * Invalid overrules all other preferences, as the user does NOT want that item to be inserted.
         */
        public ItemScroll.SortPref getHighestPreference(ItemStack stack) {
            ItemScroll.SortPref pref = ItemScroll.SortPref.LOW;
            for (FilterSet filterSet : filterSets) {
                ItemScroll.SortPref newPref = filterSet.getHighestPreference(stack);
                if (newPref == ItemScroll.SortPref.INVALID) {
                    return ItemScroll.SortPref.INVALID;
                } else if (newPref.ordinal() > pref.ordinal()) {
                    pref = newPref;
                }
            }
            return pref;
        }
    }
}
