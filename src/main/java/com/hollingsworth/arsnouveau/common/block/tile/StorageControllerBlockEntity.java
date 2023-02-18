/*
 * MIT License
 *
 * Copyright 2021 klikli-dev
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

package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.common.block.tile.container.*;
import com.hollingsworth.arsnouveau.common.network.MessageUpdateStacks;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.ForgeRegistries;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;

public class StorageControllerBlockEntity extends NetworkedBlockEntity implements MenuProvider, IStorageController, IStorageAccessor, IStorageControllerProxy, IAnimatable {

    public static final int MAX_STABILIZER_DISTANCE = 5;

    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);
    public Map<Integer, ItemStack> matrix = new HashMap<>();
    public ItemStack orderStack = ItemStack.EMPTY;
    public Map<GlobalBlockPos, MachineReference> linkedMachines = new HashMap<>();
    public Map<GlobalBlockPos, UUID> depositOrderSpirits = new HashMap<>();
    protected SortDirection sortDirection = SortDirection.DOWN;
    protected SortType sortType = SortType.AMOUNT;
    protected ItemStackHandler itemStackHandlerInternal = new StorageControllerItemStackHandler(this,
            128,
            1024,
           true
    );
    protected LazyOptional<ItemStackHandler> itemStackHandler = LazyOptional.of(() -> this.itemStackHandlerInternal);
    protected int maxSlots = 128;
    protected int usedSlots = 0;
    protected boolean stabilizersInitialized = false;
    protected GlobalBlockPos globalPos;
    protected MessageUpdateStacks cachedMessageUpdateStacks;

    public StorageControllerBlockEntity(BlockPos worldPos, BlockState state) {
        super(BlockRegistry.LECTERN_TILE.get(), worldPos, state);
    }

    public void tick() {
        if (!this.level.isClientSide) {
            if (!this.stabilizersInitialized) {
                this.stabilizersInitialized = true;

            }
        }
    }

    protected void mergeIntoList(List<ItemStack> list, ItemStack stackToAdd) {
        boolean merged = false;
        for (ItemStack stack : list) {
            if (ItemHandlerHelper.canItemStacksStack(stackToAdd, stack)) {
                stack.setCount(stack.getCount() + stackToAdd.getCount());
                merged = true;
                break;
            }
        }
        if (!merged) {
            list.add(stackToAdd);
        }
    }

    protected void validateLinkedMachines() {
        // remove all entries that lead to invalid block entities.
        this.linkedMachines.entrySet().removeIf(entry -> !entry.getValue().isValidFor(this.level));
    }

    private List<Predicate<ItemStack>> getComparatorsSortedByAmount(Predicate<ItemStack> comparator) {
        var handler = this.itemStackHandlerInternal;
        var map = new HashMap<Item, Integer>();
        for (int i = 0; i < handler.getSlots(); i++) {
            var getStackInSlot = handler.getStackInSlot(i);
            if (comparator.test(getStackInSlot)) {
                var oldCount = map.getOrDefault(getStackInSlot.getItem(), 0);
                map.put(getStackInSlot.getItem(), oldCount + getStackInSlot.getCount());
            }
        }
        return map.entrySet().stream().sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .map(entry -> (Predicate<ItemStack>) stack -> stack.getItem() == entry.getKey()).toList();
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.dimensional_matrix.new", ILoopType.EDefaultLoopTypes.LOOP));
        return PlayState.CONTINUE;
    }

    @Override
    public Component getDisplayName() {
        return Component.literal(ForgeRegistries.BLOCK_ENTITY_TYPES.getKey(this.getType()).getPath());
    }

    @Override
    public IStorageController getLinkedStorageController() {
        return this;
    }

    @Override
    public GlobalBlockPos getLinkedStorageControllerPosition() {
        if (this.globalPos == null)
            this.globalPos = new GlobalBlockPos(this.getBlockPos(), this.level);
        return this.globalPos;
    }

    @Override
    public void setLinkedStorageControllerPosition(GlobalBlockPos blockPos) {
        //Do nothing, block entity cannot move.
    }

    @Override
    public Map<Integer, ItemStack> getMatrix() {
        return this.matrix;
    }

    @Override
    public ItemStack getOrderStack() {
        return this.orderStack;
    }

    @Override
    public void setOrderStack(@Nonnull ItemStack stack) {
        this.orderStack = stack;
    }

    @Override
    public SortDirection getSortDirection() {
        return this.sortDirection;
    }

    @Override
    public void setSortDirection(SortDirection sortDirection) {
        this.sortDirection = sortDirection;
    }

    @Override
    public SortType getSortType() {
        return this.sortType;
    }

    @Override
    public void setSortType(SortType sortType) {
        this.sortType = sortType;
    }

    @Override
    public List<ItemStack> getStacks() {
        ItemStackHandler handler = this.itemStackHandlerInternal;
        int size = handler.getSlots();
        int usedSlots = 0;
        List<ItemStack> result = new ArrayList<>(size);
        for (int slot = 0; slot < size; slot++) {
            ItemStack stack = handler.getStackInSlot(slot);
            if (!stack.isEmpty()) {
                usedSlots++;
                this.mergeIntoList(result, stack.copy());
            }
        }
        this.usedSlots = usedSlots;
        return result;
    }

    @Override
    public MessageUpdateStacks getMessageUpdateStacks() {
        if (this.cachedMessageUpdateStacks == null) {
            List<ItemStack> stacks = this.getStacks();
            this.cachedMessageUpdateStacks = new MessageUpdateStacks(stacks, this.getUsedSlots(), this.getMaxSlots());
        }
        return this.cachedMessageUpdateStacks;
    }

    @Override
    public int getMaxSlots() {
        return this.maxSlots;
    }

    @Override
    public void setMaxSlots(int slots) {
        this.maxSlots = slots;
        this.itemStackHandlerInternal.setSize(this.maxSlots);
        //force resync
        this.cachedMessageUpdateStacks = null;
        this.markNetworkDirty();
    }

    @Override
    public int getUsedSlots() {
        return this.usedSlots;
    }

    @Override
    public Map<GlobalBlockPos, MachineReference> getLinkedMachines() {
        return this.linkedMachines;
    }

    @Override
    public void setLinkedMachines(Map<GlobalBlockPos, MachineReference> machines) {
        this.linkedMachines = machines;
    }

    @Override
    public void linkMachine(MachineReference machine) {
        this.linkedMachines.put(machine.insertGlobalPos, machine);
    }

    @Override
    public void addDepositOrder(GlobalBlockPos linkedMachinePosition, IItemStackComparator comparator, int amount) {
        //check if the item is available in the desired amount, otherwise kill the order.
//        ItemStack stack = this.getItemStack(comparator, amount, true);
//        if (!stack.isEmpty()) {
//            UUID spiritUUID = this.depositOrderSpirits.get(linkedMachinePosition);
//            if (spiritUUID != null) {
//                EntityUtil.getEntityByUuiDGlobal(this.level.getServer(),
//                                spiritUUID).filter(SpiritEntity.class::isInstance).map(SpiritEntity.class::cast)
//                        .ifPresent(spirit -> {
//                            Optional<ManageMachineJob> job = spirit.getJob().filter(ManageMachineJob.class::isInstance)
//                                    .map(ManageMachineJob.class::cast);
//                            if (job.isPresent()) {
//                                job.get().addDepsitOrder(new DepositOrder((ItemStackComparator) comparator, amount));
//                            } else {
//                                this.removeDepositOrderSpirit(linkedMachinePosition);
//                            }
//                        });
//            } else {
//                //if the entity cannot be found, remove it from the list for now. it will re-register itself on spawn
//                this.removeDepositOrderSpirit(linkedMachinePosition);
//            }
//        }
    }

    @Override
    public void addDepositOrderSpirit(GlobalBlockPos linkedMachinePosition, UUID spiritId) {
        this.depositOrderSpirits.put(linkedMachinePosition, spiritId);
    }

    @Override
    public void removeDepositOrderSpirit(GlobalBlockPos linkedMachinePosition) {
        this.linkedMachines.remove(linkedMachinePosition);
        this.depositOrderSpirits.remove(linkedMachinePosition);
    }

    @Override
    public boolean isBlacklisted(ItemStack stack) {
        return false;
    }

    @Override
    public int insertStack(ItemStack stack, boolean simulate) {
        if (this.isBlacklisted(stack))
            return stack.getCount();

        ItemStackHandler handler = this.itemStackHandlerInternal;
        if (ItemHandlerHelper.insertItem(handler, stack, true).getCount() < stack.getCount()) {
            stack = ItemHandlerHelper.insertItem(handler, stack, simulate);
        }

        return stack.getCount();
    }

    @Override
    public ItemStack getOneOfMostCommonItem(Predicate<ItemStack> comparator, boolean simulate) {
        if (comparator == null) {
            return ItemStack.EMPTY;
        }

        var comparators = this.getComparatorsSortedByAmount(comparator);

        ItemStackHandler handler = this.itemStackHandlerInternal;

        //we start with the comparator representing the most common item, and if we don't find anything we move on.
        //Note: unless something weird happens we should always find something.
        for (var currentComparator : comparators) {
            for (int slot = 0; slot < handler.getSlots(); slot++) {

                //first we force a simulation to check if the stack fits
                ItemStack stack = handler.extractItem(slot, 1, true);
                if (stack.isEmpty()) {
                    continue;
                }

                if (currentComparator.test(stack)) {
                    //now we do the actual operation (note: can still be a simulation, if caller wants to simulate=
                    return handler.extractItem(slot, 1, simulate);
                }

                //this slot does not match so we move on in the loop.
            }
        }

        //nothing found
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack getItemStack(Predicate<ItemStack> comparator, int requestedSize, boolean simulate) {
        if (requestedSize <= 0 || comparator == null) {
            return ItemStack.EMPTY;
        }
        ItemStackHandler handler = this.itemStackHandlerInternal;
        ItemStack firstMatchedStack = ItemStack.EMPTY;
        int remaining = requestedSize;
        for (int slot = 0; slot < handler.getSlots(); slot++) {

            //first we force a simulation
            ItemStack stack = handler.extractItem(slot, remaining, true);
            if (stack.isEmpty()) {
                continue;
            }

            //if we have not found anything yet we can just store the result or move on
            if (firstMatchedStack.isEmpty()) {

                if (!comparator.test(stack)) {
                    //this slot does not match so we move on
                    continue;
                }
                //just take entire stack -> we're in sim mode
                firstMatchedStack = stack.copy();
            } else {
                //we already found something, so we need to make sure the stacks match up, if not we move on.
                if (!ItemHandlerHelper.canItemStacksStack(firstMatchedStack, stack)) {
                    continue;
                }
            }

            //get how many we have to extract in this round, cannot be more than we need nor more than is in this slot.
            int toExtract = Math.min(stack.getCount(), remaining);

            //now we can leave simulation up to the caller
            ItemStack extractedStack = handler.extractItem(slot, toExtract, simulate);
            remaining -= extractedStack.getCount();

            //if we got all we need we can exit here.
            if (remaining <= 0) {
                break;
            }
        }

        //set the exact output count and return.
        int extractCount = requestedSize - remaining;
        if (!firstMatchedStack.isEmpty() && extractCount > 0) {
            firstMatchedStack.setCount(extractCount);
        }

        return firstMatchedStack;
    }

    public int getAvailableAmount(IItemStackComparator comparator) {
        if (comparator == null) {
            return 0;
        }
        int totalCount = 0;
        ItemStackHandler handler = this.itemStackHandlerInternal;
        int size = handler.getSlots();
        for (int slot = 0; slot < size; slot++) {
            ItemStack stack = handler.getStackInSlot(slot);
            if (comparator.matches(stack))
                totalCount += stack.getCount();
        }
        return totalCount;
    }

    @Override
    public void onContentsChanged() {
        this.cachedMessageUpdateStacks = null;
        this.setChanged();
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        this.itemStackHandler.invalidate();
    }

    @Override
    public void reviveCaps() {
        super.reviveCaps();
        this.itemStackHandler = LazyOptional.of(() -> this.itemStackHandlerInternal);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, Direction direction) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return this.itemStackHandler.cast();
        }
        return super.getCapability(cap, direction);
    }

    @Override
    public void load(CompoundTag compound) {
        compound.remove("linkedMachines"); //linked machines are not saved, they self-register.
        super.load(compound);

        //read stored items
        if (compound.contains("items")) {
            this.itemStackHandlerInternal.deserializeNBT(compound.getCompound("items"));
            this.cachedMessageUpdateStacks = null;
        }
    }

    @Override
    protected void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        compound.remove("linkedMachines"); //linked machines are not saved, they self-register.
        compound.put("items", this.itemStackHandlerInternal.serializeNBT());
    }

    @Override
    public void loadNetwork(CompoundTag compound) {
        this.setSortDirection(SortDirection.get(compound.getInt("sortDirection")));
        this.setSortType(SortType.get(compound.getInt("sortType")));

        if (compound.contains("maxSlots")) {
            this.setMaxSlots(compound.getInt("maxSlots"));
        }

        //read stored crafting matrix
        this.matrix = new HashMap<Integer, ItemStack>();
        if (compound.contains("matrix")) {
            ListTag matrixNbt = compound.getList("matrix", Tag.TAG_COMPOUND);
            for (int i = 0; i < matrixNbt.size(); i++) {
                CompoundTag stackTag = matrixNbt.getCompound(i);
                int slot = stackTag.getByte("slot");
                ItemStack s = ItemStack.of(stackTag);
                this.matrix.put(slot, s);
            }
        }

        if (compound.contains("orderStack"))
            this.orderStack = ItemStack.of(compound.getCompound("orderStack"));

        //read the linked machines
        this.linkedMachines = new HashMap<>();
        if (compound.contains("linkedMachines")) {
            ListTag machinesNbt = compound.getList("linkedMachines", Tag.TAG_COMPOUND);
            for (int i = 0; i < machinesNbt.size(); i++) {
                MachineReference reference = MachineReference.from(machinesNbt.getCompound(i));
                this.linkedMachines.put(reference.insertGlobalPos, reference);
            }
        }
    }

    @Override
    public CompoundTag saveNetwork(CompoundTag compound) {
        compound.putInt("sortDirection", this.getSortDirection().getValue());
        compound.putInt("sortType", this.getSortType().getValue());
        compound.putInt("maxSlots", this.maxSlots);

        //write stored crafting matrix
        ListTag matrixNbt = new ListTag();
        for (int i = 0; i < 9; i++) {
            if (this.matrix.get(i) != null && !this.matrix.get(i).isEmpty()) {
                CompoundTag stackTag = new CompoundTag();
                stackTag.putByte("slot", (byte) i);
                this.matrix.get(i).save(stackTag);
                matrixNbt.add(stackTag);
            }
        }
        compound.put("matrix", matrixNbt);

        if (!this.orderStack.isEmpty())
            compound.put("orderStack", this.orderStack.save(new CompoundTag()));

        //write linked machines
        ListTag machinesNbt = new ListTag();
        for (Map.Entry<GlobalBlockPos, MachineReference> entry : this.linkedMachines.entrySet()) {
            machinesNbt.add(entry.getValue().serializeNBT());
        }
        compound.put("linkedMachines", machinesNbt);

        return compound;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player playerEntity) {
        return new StorageControllerContainer(id, playerInventory, this);
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController(this, "controller", 0, this::predicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

}
