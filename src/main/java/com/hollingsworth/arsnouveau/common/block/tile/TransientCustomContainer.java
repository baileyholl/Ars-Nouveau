package com.hollingsworth.arsnouveau.common.block.tile;

import net.minecraft.core.NonNullList;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;

// A copy of TransientCraftingContainer with some tweaks to support grid manipulation without update
public class TransientCustomContainer extends CraftingContainer {

    public TransientCustomContainer(AbstractContainerMenu pMenu, int pWidth, int pHeight) {
        this(pMenu, pWidth, pHeight, NonNullList.withSize(pWidth * pHeight, ItemStack.EMPTY));
    }

    public TransientCustomContainer(AbstractContainerMenu pMenu, int pWidth, int pHeight, NonNullList<ItemStack> pItems) {
        super(pMenu, pWidth, pHeight);
        this.items = pItems;
    }

    /**
     * Removes a stack from the given slot and returns it.
     */
    public ItemStack removeItemNoUpdate(int pSlot, int pAmount) {
        return ContainerHelper.removeItem(this.items, pSlot, pAmount);
    }

    /**
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     */
    public void setItemNoUpdate(int pSlot, ItemStack pStack) {
        this.items.set(pSlot, pStack);
    }
}
