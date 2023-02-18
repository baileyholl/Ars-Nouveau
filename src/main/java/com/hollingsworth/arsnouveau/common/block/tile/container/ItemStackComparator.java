/*
 * MIT License
 *
 * Copyright 2020 klikli-dev, MrRiegel, Sam Bassettl.
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
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * Based on https://github.com/Lothrazar/Storage-Network
 */
public class ItemStackComparator implements IItemStackComparator {
    //region Fields
    protected ItemStack filterStack;
    protected boolean matchNbt;
    //endregion Fields

    //region Initialization
    public ItemStackComparator(ItemStack stack) {
        this(stack, false);
    }

    public ItemStackComparator(ItemStack filterStack, boolean matchNbt) {
        this.filterStack = filterStack;
        this.matchNbt = matchNbt;
    }

    private ItemStackComparator() {
    }
    //endregion Initialization

    //region Static Methods
    public static ItemStackComparator from(CompoundTag nbt) {
        ItemStackComparator comparator = new ItemStackComparator();
        comparator.deserializeNBT(nbt);
        return !comparator.filterStack.isEmpty() ? comparator : null;
    }

    //region Getter / Setter
    public boolean getMatchNbt() {
        return this.matchNbt;
    }

    public void setMatchNbt(boolean matchNbt) {
        this.matchNbt = matchNbt;
    }

    public ItemStack getFilterStack() {
        return this.filterStack;
    }
    //endregion Getter / Setter

    public void setFilterStack(@Nonnull ItemStack filterStack) {
        this.filterStack = filterStack;
    }

    //region Overrides
    @Override
    public boolean matches(@Nonnull ItemStack stack) {
        if (stack.isEmpty())
            return false;

        if (this.matchNbt && !ItemStack.isSameItemSameTags(this.filterStack, stack))
            return false;
        return stack.getItem() == this.filterStack.getItem();
    }

    @Override
    public CompoundTag serializeNBT() {
        return this.write(new CompoundTag());
    }
    //endregion Overrides

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.read(nbt);
    }
    //endregion Static Methods

    //region Methods
    public void read(CompoundTag compound) {
        CompoundTag nbt = compound.getCompound("stack");
        this.filterStack = ItemStack.of(nbt);
        this.matchNbt = compound.getBoolean("matchNbt");
    }

    public CompoundTag write(CompoundTag compound) {
        compound.put("stack", this.filterStack.save(new CompoundTag()));
        compound.putBoolean("matchNbt", this.matchNbt);
        return compound;
    }
    //endregion Methods
}
