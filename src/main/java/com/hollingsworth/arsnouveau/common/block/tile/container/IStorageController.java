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

import com.hollingsworth.arsnouveau.common.network.MessageUpdateStacks;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;

public interface IStorageController {
    //region Getter / Setter

    /**
     * Gets a list of all stacks in the storage controller. Use sparingly! This list can be large and thus cause a lot
     * of network traffic as well as performance issues in collecting it.
     *
     * @return all stacks available in the storage controller.
     */
    List<ItemStack> getStacks();

    /**
     * Gets a message to update stacks, re-uses cached messages whenever possible
     *
     * @return the stack update message.
     */
    MessageUpdateStacks getMessageUpdateStacks();

    /**
     * @return the max slots available in this storage controller.
     */
    int getMaxSlots();

    /**
     * @param slots the max slots available in the storage controller.
     */
    void setMaxSlots(int slots);

    /**
     * @return the used up slots. Usually lazily updated when calling getStacks.
     */
    int getUsedSlots();

    /**
     * @return a list of block entity references for the machines liked for autocrafting
     */
    Map<GlobalBlockPos, MachineReference> getLinkedMachines();

    /**
     * @param machines @return a list of block entity references for the machines liked for autocrafting
     */
    void setLinkedMachines(Map<GlobalBlockPos, MachineReference> machines);
    //endregion Getter / Setter

    //region Methods

    /**
     * @param machine the machine to add
     */
    void linkMachine(MachineReference machine);

    /**
     * Creates an order to depositing the given amount of items matching the given comparator in the machine at the
     * given position.
     *
     * @param linkedMachinePosition the position of the machine to deposit in.
     * @param comparator            the item stack comparator.
     * @param amount                the amount to deposit
     */
    void addDepositOrder(GlobalBlockPos linkedMachinePosition, IItemStackComparator comparator, int amount);

    /**
     * Adds the given spirit uuid to the list of spirits to give orders to.
     *
     * @param linkedMachinePosition the position of the machine to deposit in.
     * @param spiritId              the uuid of the spirit
     */
    void addDepositOrderSpirit(GlobalBlockPos linkedMachinePosition, UUID spiritId);

    /**
     * Removes the given spirit uuid from the list of spirits to give orders to.
     *
     * @param linkedMachinePosition the position of the machine to deposit in.
     */
    void removeDepositOrderSpirit(GlobalBlockPos linkedMachinePosition);

    /**
     * @param stack the stack to check.
     * @return true of the stack cannot be added to the controller.
     */
    boolean isBlacklisted(ItemStack stack);

    /**
     * Inserts the given stack into the controller.
     *
     * @param stack    the stack to insert into the network
     * @param simulate true for simulation
     * @return count of remaining items in the stack.
     */
    int insertStack(ItemStack stack, boolean simulate);

    /**
     * Gets a stack with the size 1 of the most common item in the storage controller fitting the comparator.
     *
     * @param comparator the comparator to match against.
     * @param simulate   true for simulation.
     * @return the matching item stack.
     */
    ItemStack getOneOfMostCommonItem(Predicate<ItemStack> comparator, boolean simulate);

    /**
     * Gets the matching itemstack from the controller.
     *
     * @param comparator    the comparator to match against.
     * @param requestedSize the stack size to get
     * @param simulate      true for simulation
     * @return the matching item stack.
     */
    ItemStack getItemStack(Predicate<ItemStack> comparator, int requestedSize, boolean simulate);

    /**
     * Gets the available amount of the matching item stack in the controller.
     *
     * @param comparator the comparator to match against.
     * @return the total available amount
     */
    int getAvailableAmount(IItemStackComparator comparator);

    /**
     * Called when the contents of the storage controller change.
     */
    void onContentsChanged();
    //endregion Methods
}
