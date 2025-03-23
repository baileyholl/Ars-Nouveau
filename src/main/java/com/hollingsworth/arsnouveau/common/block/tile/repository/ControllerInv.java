package com.hollingsworth.arsnouveau.common.block.tile.repository;

import com.hollingsworth.arsnouveau.api.item.inv.CombinedHandlerInv;
import com.hollingsworth.arsnouveau.api.item.inv.IMapInventory;
import com.hollingsworth.arsnouveau.common.items.ItemScroll;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Predicate;

public class ControllerInv extends CombinedHandlerInv implements IMapInventory {
    public RepositoryControllerTile controllerTile;

    public ControllerInv(RepositoryControllerTile controllerTile, IItemHandler... itemHandler)
    {
        super(itemHandler);
        this.controllerTile = controllerTile;
    }

    @Override
    public ItemStack insertStack(ItemStack stack, boolean simulate) {
        List<RepositoryControllerTile.ConnectedRepository> validRepositories = controllerTile.preferredForStack(stack, false);
        // Prefer inserting into existing stacks first, splitting across as many inventories as needed
        for(RepositoryControllerTile.ConnectedRepository connectedRepository : validRepositories){
            IMapInventory connected = connectedRepository.capability.getCapability();
            if(connected != null && connected.hasExistingSlotsForInsertion(stack)){
                ItemStack remainder = connected.insertStack(stack, simulate);
                if(remainder.isEmpty()){
                    return ItemStack.EMPTY;
                }
                stack = remainder;
            }
        }

        // Fall back to inserting into any empty slots.
        for(RepositoryControllerTile.ConnectedRepository connectedRepository : validRepositories){
            IMapInventory connected = connectedRepository.capability.getCapability();
            if(connected != null && connected.hasExistingSlotsForInsertion(ItemStack.EMPTY)){
                ItemStack remainder = connected.insertStack(stack, simulate);
                if(remainder.isEmpty()){
                    return ItemStack.EMPTY;
                }
                stack = remainder;
            }
        }

        return stack;
    }

    @Override
    public boolean hasExistingSlotsForInsertion(ItemStack stack) {
        for(RepositoryControllerTile.ConnectedRepository connectedRepository : controllerTile.connectedRepositories){
            IMapInventory connected = connectedRepository.capability.getCapability();
            if(connected != null && connected.hasExistingSlotsForInsertion(stack)){
                return true;
            }
        }
        return false;
    }

    @Override
    public ItemStack extractByItem(Item item, int count, boolean simulate, Predicate<ItemStack> filter) {
        for(RepositoryControllerTile.ConnectedRepository connectedRepository : controllerTile.connectedRepositories){
            IMapInventory connected = connectedRepository.capability.getCapability();
            if(connected != null){
                ItemStack extracted = connected.extractByItem(item, count, simulate, filter);
                if(!extracted.isEmpty()){
                    return extracted;
                }
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemScroll.SortPref getInsertionPreference(ItemStack stack) {
        List<RepositoryControllerTile.ConnectedRepository> validRepositories = controllerTile.preferredForStack(stack, false);
        if(validRepositories.isEmpty())
            return ItemScroll.SortPref.INVALID;
        return validRepositories.getFirst().capability.getCapability().getInsertionPreference(stack);
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        return insertStack(stack, simulate);
    }
}
