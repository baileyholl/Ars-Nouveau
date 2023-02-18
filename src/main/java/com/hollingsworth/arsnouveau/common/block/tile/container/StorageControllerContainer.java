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

import com.hollingsworth.arsnouveau.common.block.tile.StorageControllerBlockEntity;
import com.hollingsworth.arsnouveau.common.menu.MenuRegistry;
import com.hollingsworth.arsnouveau.common.network.MessageUpdateLinkedMachines;
import com.hollingsworth.arsnouveau.common.network.Networking;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;


public class StorageControllerContainer extends StorageControllerContainerBase {
    //region Fields
    protected StorageControllerBlockEntity storageController;
    //endregion Fields

    //region Initialization
    public StorageControllerContainer(int id, Inventory playerInventory,
                                      StorageControllerBlockEntity storageController) {
        super(MenuRegistry.LECTERN.get(), id, playerInventory);
        this.storageController = storageController;

        this.matrix = new StorageControllerCraftingInventory(this, storageController.getMatrix());
        this.orderInventory.setItem(0, storageController.getOrderStack());

        this.setupCraftingOutput(); //output is slot 0

        this.setupCraftingGrid();
        this.setupOrderInventorySlot();
        this.setupPlayerInventorySlots();
        this.setupPlayerHotbar();

        this.slotsChanged(this.matrix);
    }
    //endregion Initialization

    //region Overrides
    @Override
    protected void setupPlayerHotbar() {
        int hotbarTop = 232;
        int hotbarLeft = 8 + ORDER_AREA_OFFSET;
        for (int i = 0; i < 9; i++)
            this.addSlot(new Slot(this.playerInventory, i, hotbarLeft + i * 18, hotbarTop));
    }

    @Override
    public StorageControllerBlockEntity getStorageController() {
        return this.storageController;
    }

    @Override
    public boolean isContainerItem() {
        return false;
    }

    @Override
    public void updateCraftingSlots(boolean force) {
        for (int i = 0; i < 9; i++) {
            this.storageController.getMatrix().put(i, this.matrix.getItem(i));
        }
        if (force)
            this.storageController.markNetworkDirty();
    }

    @Override
    public void updateOrderSlot(boolean force) {
        this.storageController.setOrderStack(this.orderInventory.getItem(0));
        if (force)
            this.storageController.markNetworkDirty();
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack stack, Slot slot) {
        return slot.container != this.result && super.canTakeItemForPickAll(stack, slot);
    }

    @Override
    public boolean stillValid(Player player) {
        if (this.storageController == null)
            return false;

        Level level = this.storageController.getLevel();
        BlockPos controllerPosition = this.storageController.getBlockPos();

        //close container if block is destroyed
        if (level.getBlockEntity(controllerPosition) != this.storageController)
            return false;

        //send stack updates on a slow tick while interacting
        if (!level.isClientSide && level.getGameTime() % 40 == 0) {
            Networking.sendTo((ServerPlayer) player, this.storageController.getMessageUpdateStacks());
            Networking.sendTo((ServerPlayer) player,
                    new MessageUpdateLinkedMachines(this.storageController.getLinkedMachines()));
        }

        //prevent player from interacting with the container if the controller is not in range
        return player.distanceToSqr(controllerPosition.getX() + 0.5D, controllerPosition.getY() + 0.5D,
                controllerPosition.getZ() + 0.5D) <= 64.0D;
    }
    //endregion Overrides
}
