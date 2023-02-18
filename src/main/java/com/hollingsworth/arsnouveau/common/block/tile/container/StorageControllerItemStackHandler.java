/*
 * MIT License
 *
 * Copyright 2020 klikli-dev
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 * OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

package com.hollingsworth.arsnouveau.common.block.tile.container;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import java.util.Collections;

public class StorageControllerItemStackHandler extends ItemStackHandler {

    //region Fields
    protected int maxStackSize;
    protected int maxSlots;
    protected boolean overrideItemStackSizes;
    protected IStorageController storageController;
    //endregion Fields

    //region Initialization
    public StorageControllerItemStackHandler(IStorageController storageController, int size, int maxStackSize,
                                             boolean overrideItemStackSizes) {
        super();
        this.stacks = NonNullArrayList.withSize(size, ItemStack.EMPTY);
        this.storageController = storageController;
        this.maxSlots = size;
        this.maxStackSize = maxStackSize;
        this.overrideItemStackSizes = overrideItemStackSizes;
    }
    //endregion Initialization

    //region Overrides
    @Override
    public void setSize(int size) {
        if (size < 0)
            return;
        //store new desired size
        this.maxSlots = size;

        //if we need to increase, we simply copy to a bigger list
        if (size > this.stacks.size()) {
            //add empty item stacks until we are full
            this.stacks.addAll(Collections.nCopies(size - this.stacks.size(), ItemStack.EMPTY));
        }
        //if list got smaller we prune what wer can, the rest is removed ony by one when a full stack is removed
        else if (size < this.stacks.size()) {
            this.prune();
        }
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (slot > this.maxSlots - 1) //if we are oversized, do not allow insertion into the overhead.
            return stack;
        return super.insertItem(slot, stack, simulate);
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        ItemStack result = this.extractItemOverride(slot, amount, simulate);

        if (!simulate && this.stacks.size() > this.maxSlots) {
            //in real mode if we are above desired size, delete empty slots.
            if (this.stacks.get(slot).isEmpty())
                this.stacks.remove(slot);
        }

        return result;
    }

    //Logic from super.extractItem modified to allow for custom stack sizes
    public ItemStack extractItemOverride(int slot, int amount, boolean simulate) {
        if (amount == 0)
            return ItemStack.EMPTY;

        this.validateSlotIndex(slot);

        ItemStack existing = this.stacks.get(slot);

        if (existing.isEmpty())
            return ItemStack.EMPTY;

        //only change to forge's method -> instead of just using max stack size, we refer to our stack limit which can be overridden via config.
        int toExtract = Math.min(amount, this.getStackLimit(-1, existing));

        if (existing.getCount() <= toExtract) {
            if (!simulate) {
                this.stacks.set(slot, ItemStack.EMPTY);
                this.onContentsChanged(slot);
                return existing;
            } else {
                return existing.copy();
            }
        } else {
            if (!simulate) {
                this.stacks.set(slot, ItemHandlerHelper.copyStackWithSize(existing, existing.getCount() - toExtract));
                this.onContentsChanged(slot);
            }

            return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
        }
    }

    @Override
    public int getSlotLimit(int slot) {
        return this.maxStackSize;
    }

    @Override
    protected int getStackLimit(int slot, @Nonnull ItemStack stack) {
        return this.overrideItemStackSizes ? this.getSlotLimit(slot) : Math.min(this.getSlotLimit(slot),
                stack.getMaxStackSize());
    }

    @Override
    public CompoundTag serializeNBT() {
        ListTag nbtTagList = new ListTag();
        for (int i = 0; i < this.stacks.size(); i++) {
            if (!this.stacks.get(i).isEmpty()) {
                CompoundTag itemTag = new CompoundTag();
                ItemStack stack = this.stacks.get(i);
                itemTag.putInt("Slot", i);
                stack.save(itemTag);
                itemTag.putInt("RealSize", stack.getCount());
                nbtTagList.add(itemTag);
            }
        }
        CompoundTag nbt = new CompoundTag();
        nbt.put("Items", nbtTagList);
        nbt.putInt("Size", this.stacks.size());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.setSize(nbt.contains("Size", Tag.TAG_INT) ? nbt.getInt("Size") : this.stacks.size());
        ListTag tagList = nbt.getList("Items", Tag.TAG_COMPOUND);
        for (int i = 0; i < tagList.size(); i++) {
            CompoundTag itemTags = tagList.getCompound(i);
            int slot = itemTags.getInt("Slot");

            if (slot >= 0 && slot < this.stacks.size()) {
                ItemStack stack = ItemStack.of(itemTags);
                stack.setCount(itemTags.getInt("RealSize"));
                this.stacks.set(slot, stack);
            }
        }
        this.onLoad();
    }

    @Override
    protected void onContentsChanged(int slot) {
        this.storageController.onContentsChanged();
    }

    //region Methods
    public void prune() {
        //iterate from the end and remove empty stuff until we have our desired size
        for (int i = this.stacks.size() - 1; i >= 0 && this.stacks.size() > this.maxSlots; i--) {
            if (this.stacks.get(i).isEmpty())
                this.stacks.remove(i);
        }
    }
    //endregion Methods

}
