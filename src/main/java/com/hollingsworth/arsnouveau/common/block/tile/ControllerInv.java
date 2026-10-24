package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.item.inv.CombinedHandlerInv;
import com.hollingsworth.arsnouveau.api.item.inv.FilterSet;
import com.hollingsworth.arsnouveau.api.item.inv.IMapInventory;
import com.hollingsworth.arsnouveau.common.items.ItemScroll;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

public class ControllerInv extends CombinedHandlerInv implements IMapInventory {
    public RepositoryCatalogTile controllerTile;

    public ControllerInv(RepositoryCatalogTile controllerTile, IItemHandler... itemHandler) {
        super(itemHandler);
        this.controllerTile = controllerTile;
    }

    @Override
    public ItemStack insertStack(ItemStack stack, boolean simulate) {
        record InsertionTarget(SortResult repository, boolean holdsStack) {
        }
        List<InsertionTarget> targets = new ArrayList<>();
        
        for (SortResult connectedRepository : preferredForStack(stack, false)) {
            IMapInventory connected = connectedRepository.mapInventory();
            boolean holdsStack = connected.hasExistingSlotsForInsertion(stack);
            if (holdsStack || connected.hasExistingSlotsForInsertion(ItemStack.EMPTY)) {
                targets.add(new InsertionTarget(connectedRepository, holdsStack));
            }
        }
        // Prefer inserting into existing stacks first, splitting across as many inventories as needed
        targets.sort(Comparator.comparing(InsertionTarget::repository, SortResult.comparator)
                .thenComparing(InsertionTarget::holdsStack, Comparator.reverseOrder()));
        for (InsertionTarget target : targets) {
            stack = target.repository().mapInventory().insertStack(stack, simulate);
            if (stack.isEmpty()) {
                return ItemStack.EMPTY;
            }
        }
        return stack;
    }

    @Override
    public boolean hasExistingSlotsForInsertion(ItemStack stack) {
        for (RepositoryCatalogTile.ConnectedRepository connectedRepository : controllerTile.connectedRepositories) {
            IMapInventory connected = connectedRepository.capability.getCapability();
            if (connected != null && connected.hasExistingSlotsForInsertion(stack)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public ItemStack extractByItem(Item item, int count, boolean simulate, Predicate<ItemStack> filter) {
        for (RepositoryCatalogTile.ConnectedRepository connectedRepository : controllerTile.connectedRepositories) {
            IMapInventory connected = connectedRepository.capability.getCapability();
            if (connected != null) {
                ItemStack extracted = connected.extractByItem(item, count, simulate, filter);
                if (!extracted.isEmpty()) {
                    return extracted;
                }
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        return super.extractItem(slot, amount, simulate);
    }

    @Override
    public ItemScroll.SortPref getInsertionPreference(ItemStack stack) {
        List<SortResult> validRepositories = preferredForStack(stack, false);
        if (validRepositories.isEmpty())
            return ItemScroll.SortPref.INVALID;
        return validRepositories.getFirst().mapInventory().getInsertionPreference(stack);
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        return insertStack(stack, simulate);
    }

    public List<SortResult> preferredForStack(ItemStack stack, boolean includeInvalid) {
        List<SortResult> filtered = new ArrayList<>();
        if (!allowedByFilter(stack))
            return filtered;
        for (var connectedRepository : controllerTile.connectedRepositories) {
            var cap = connectedRepository.capability;
            if (cap == null || cap.getCapability() == null)
                continue;
            ItemScroll.SortPref sortPref = cap.getCapability().getInsertionPreference(stack);
            if (includeInvalid || (sortPref != ItemScroll.SortPref.INVALID)) {
                filtered.add(new SortResult(sortPref, connectedRepository));
            }
        }
        filtered.sort(SortResult.comparator);
        return filtered;
    }

    public boolean allowedByFilter(ItemStack stack) {
        if (controllerTile.scrollStack.isEmpty() || stack.isEmpty()) {
            return true;
        }
        var set = new FilterSet.ListSet();
        set.addFilterScroll(controllerTile.scrollStack, this);
        return set.getHighestPreference(stack) != ItemScroll.SortPref.INVALID;
    }

    public record SortResult(ItemScroll.SortPref sortPref,
                             @NotNull RepositoryCatalogTile.ConnectedRepository connectedRepository) {
        /**
         * Comparator for sorting SortResult by their sort preference in descending order, i.e. higher preferences come first.
         */
        public static Comparator<SortResult> comparator = Comparator.comparing(SortResult::sortPref, ItemScroll.sortPrefComparator.reversed());

        public BlockCapabilityCache<IMapInventory, Direction> capability() {
            return connectedRepository.capability;
        }

        public IMapInventory mapInventory() {
            return connectedRepository.capability.getCapability();
        }
    }
}
