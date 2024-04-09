package com.hollingsworth.arsnouveau.common.block.tile;

import net.minecraft.core.NonNullList;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;

import java.util.List;
// A copy of TransientCraftingContainer with some tweaks to support grid manipulation without update
public class TransientCustomContainer implements CraftingContainer {
    private final NonNullList<ItemStack> items;
    private final int width;
    private final int height;
    private final AbstractContainerMenu menu;

    public TransientCustomContainer(AbstractContainerMenu pMenu, int pWidth, int pHeight) {
        this(pMenu, pWidth, pHeight, NonNullList.withSize(pWidth * pHeight, ItemStack.EMPTY));
    }

    public TransientCustomContainer(AbstractContainerMenu pMenu, int pWidth, int pHeight, NonNullList<ItemStack> pItems) {
        this.items = pItems;
        this.menu = pMenu;
        this.width = pWidth;
        this.height = pHeight;
    }

    /**
     * Returns the number of slots in the inventory.
     */
    public int getContainerSize() {
        return this.items.size();
    }

    public boolean isEmpty() {
        for(ItemStack itemstack : this.items) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns the stack in the given slot.
     */
    public ItemStack getItem(int pSlot) {
        return pSlot >= this.getContainerSize() ? ItemStack.EMPTY : this.items.get(pSlot);
    }

    /**
     * Removes a stack from the given slot and returns it.
     */
    public ItemStack removeItemNoUpdate(int pSlot) {
        return ContainerHelper.takeItem(this.items, pSlot);
    }

    /**
     * Removes a stack from the given slot and returns it.
     */
    public ItemStack removeItemNoUpdate(int pSlot, int pAmount) {
        return ContainerHelper.removeItem(this.items, pSlot, pAmount);
    }


    /**
     * Removes up to a specified number of items from an inventory slot and returns them in a new stack.
     */
    public ItemStack removeItem(int pSlot, int pAmount) {
        ItemStack itemstack = ContainerHelper.removeItem(this.items, pSlot, pAmount);
        if (!itemstack.isEmpty()) {
            this.menu.slotsChanged(this);
        }

        return itemstack;
    }

    /**
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     */
    public void setItem(int pSlot, ItemStack pStack) {
        this.items.set(pSlot, pStack);
        this.menu.slotsChanged(this);
    }


    /**
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     */
    public void setItemNoUpdate(int pSlot, ItemStack pStack) {
        this.items.set(pSlot, pStack);
    }

    /**
     * For block entities, ensures the chunk containing the block entity is saved to disk later - the game won't think it
     * hasn't changed and skip it.
     */
    public void setChanged() {
    }

    /**
     * Don't rename this method to canInteractWith due to conflicts with Container
     */
    public boolean stillValid(Player pPlayer) {
        return true;
    }

    public void clearContent() {
        this.items.clear();
    }

    public int getHeight() {
        return this.height;
    }

    public int getWidth() {
        return this.width;
    }

    public List<ItemStack> getItems() {
        return List.copyOf(this.items);
    }

    public void fillStackedContents(StackedContents pContents) {
        for(ItemStack itemstack : this.items) {
            pContents.accountSimpleStack(itemstack);
        }

    }
}
