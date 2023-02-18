/*
 * MIT License
 *
 * Copyright 2020 klikli-dev, MrRiegel, Sam Bassett
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

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Extension of slot crafting that sends network updates. Based on https://github.com/Lothrazar/Storage-Network
 */
public class StorageControllerSlot extends ResultSlot {
    //region Fields
    IStorageControllerContainer storageControllerContainer;
    CraftingContainer matrix;
    //endregion Fields

    //region Initialization
    public StorageControllerSlot(Player player, CraftingContainer matrix, Container inventory,
                                 IStorageControllerContainer storageControllerContainer, int slotIndex, int xPosition,
                                 int yPosition) {
        super(player, matrix, inventory, slotIndex, xPosition, yPosition);
        this.storageControllerContainer = storageControllerContainer;
        this.matrix = matrix;
    }
    //endregion Initialization

    //region Overrides

    @Override
    public void onTake(Player player, ItemStack stack) {
        if (player.level.isClientSide) {
            // return stack;
            return;
        }

        List<ItemStack> craftingStacks = new ArrayList<>();
        for (int i = 0; i < this.matrix.getContainerSize(); i++) {
            craftingStacks.add(this.matrix.getItem(i).copy());
        }
        super.onTake(player, stack);
        ((AbstractContainerMenu) this.storageControllerContainer).broadcastChanges();
        for (int i = 0; i < this.matrix.getContainerSize(); i++) {
            IStorageController storageController = this.storageControllerContainer.getStorageController();
            if (this.matrix.getItem(i).isEmpty() && storageController != null) {
                ItemStack req = storageController.getItemStack(
                        !craftingStacks.get(i).isEmpty() ? new ItemStackComparator(craftingStacks.get(i)) : null, 1,
                        false);
                if (!req.isEmpty()) {
                    this.matrix.setItem(i, req);
                }
            }
        }
        ((AbstractContainerMenu) this.storageControllerContainer).broadcastChanges();
        //return stack;
    }
    //endregion Overrides

}
