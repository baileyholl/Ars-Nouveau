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

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.CraftingContainer;

public interface IStorageControllerContainer {
    //region Getter / Setter
    IStorageController getStorageController();

    GlobalBlockPos getStorageControllerGlobalBlockPos();

    CraftingContainer getCraftMatrix();

    SimpleContainer getOrderSlot();

    /**
     * @return true if this is an item based access to the controller, false if it is a block.
     */
    boolean isContainerItem();
    //endregion Getter / Setter

    //region Methods

    /**
     * Updates the crafting slots.
     *
     * @param force forces a network update
     */
    void updateCraftingSlots(boolean force);

    /**
     * Updates the order slot;
     *
     * @param force forces a network update
     */
    void updateOrderSlot(boolean force);

    //endregion Methods
}
