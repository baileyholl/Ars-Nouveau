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

import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * A cached version of InventoryCrafting. Allows to control when events are sent with the disableEvents field.
 */
public class StorageControllerCraftingInventory extends CraftingContainer {

    //region Fields
    protected static Field itemsField;

    static {
        itemsField = ObfuscationReflectionHelper.findField(CraftingContainer.class, "f_39320_");
    }

    protected final AbstractContainerMenu container;
    /**
     * set to true to disable sending events (usually temporarily to perform multiple actions)
     */
    public boolean disableEvents;
    //endregion Fields

    //region Initialization
    public StorageControllerCraftingInventory(AbstractContainerMenu container, int width, int height) {
        super(container, width, height);
        this.container = container;
    }

    public StorageControllerCraftingInventory(AbstractContainerMenu container, Map<Integer, ItemStack> matrix) {
        this(container, 3, 3);
        this.disableEvents = true;
        for (int i = 0; i < this.getWidth() * this.getHeight(); i++) {
            if (matrix.get(i) != null && !matrix.get(i).isEmpty())
                this.setItem(i, matrix.get(i));
        }
        this.disableEvents = false;
    }

    public StorageControllerCraftingInventory(AbstractContainerMenu container, List<ItemStack> matrix) {
        this(container, 3, 3);
        this.disableEvents = true;
        for (int i = 0; i < matrix.size(); i++) {
            if (matrix.get(i) != null && !matrix.get(i).isEmpty())
                this.setItem(i, matrix.get(i));
        }
        this.disableEvents = false;
    }
    //endregion Initialization

    //region Overrides

    @SuppressWarnings("unchecked")
    @Override
    public void setItem(int index, ItemStack stack) {
        try {
            ((NonNullList<ItemStack>) itemsField.get(this)).set(index, stack);
            //only notify if events are enabled
            if (!this.disableEvents) {
                this.container.slotsChanged(this);
            }
        } catch (IllegalAccessException ignored) {
        }
    }

    //endregion Overrides
}
