package com.hollingsworth.arsnouveau.client.container;

import com.google.common.collect.Lists;
import com.hollingsworth.arsnouveau.common.block.tile.CraftingLecternTile;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.ServerToClientStoragePacket;
import com.hollingsworth.arsnouveau.common.network.SetTerminalSettingsPacket;
import com.hollingsworth.arsnouveau.common.network.UpdateStorageItemsPacket;
import com.hollingsworth.arsnouveau.setup.registry.MenuRegistry;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.recipebook.ServerPlaceRecipe;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.apache.commons.lang3.math.Fraction;

import javax.annotation.Nullable;
import java.util.*;

public class CraftingTerminalMenu extends RecipeBookMenu<CraftingInput, CraftingRecipe> implements IAutoFillTerminal {
    public static class SlotCrafting extends Slot {
        public boolean active;

        public SlotCrafting(Container inventoryIn, int index, int xPosition, int yPosition) {
            super(inventoryIn, index, xPosition, yPosition);
            active = true;
        }

        @Override
        public boolean isActive() {
            return super.isActive() && active;
        }
    }

    public static class ActiveResultSlot extends ResultSlot {
        public boolean active;

        public ActiveResultSlot(Player pPlayer, CraftingContainer pCraftSlots, Container pContainer, int pSlot, int pXPosition, int pYPosition) {
            super(pPlayer, pCraftSlots, pContainer, pSlot, pXPosition, pYPosition);
            active = true;
        }

        @Override
        public boolean isActive() {
            return active;
        }
    }

    public enum SlotAction {
        PULL_OR_PUSH_STACK,
        PULL_ONE,
        SPACE_CLICK,
        SHIFT_PULL,
        GET_HALF,
        GET_QUARTER
    }

    protected CraftingLecternTile te;
    protected int playerSlotsStart;
    protected List<SlotStorage> storageSlotList = new ArrayList<>();
    public List<StoredItemStack> itemList = new ArrayList<>();
    protected Inventory pinv;
    public String search;
    public Map<StoredItemStack, StoredItemStack> itemMap = new HashMap<>();
    boolean sentSettings = false;
    public Map<UUID, String> tabs = new HashMap<>();
    private Object2LongMap<StoredItemStack> itemLongMap = new Object2LongOpenHashMap<>();
    protected List<SlotCrafting> craftSlotList = new ArrayList<>();
    private final CraftingContainer craftMatrix;
    private final ResultContainer craftResult;
    private ActiveResultSlot craftingResultSlot;
    private final List<ContainerListener> listeners = Lists.newArrayList();

    @Override
    public void addSlotListener(ContainerListener listener) {
        super.addSlotListener(listener);
        listeners.add(listener);
    }

    @Override
    public void removeSlotListener(ContainerListener listener) {
        super.removeSlotListener(listener);
        listeners.remove(listener);
    }

    public List<ItemStack> getItemsInCraftingSlots() {
        return this.craftMatrix.getItems();
    }

    public CraftingTerminalMenu(int id, Inventory inv, CraftingLecternTile te) {
        super(MenuRegistry.STORAGE.get(), id);
        this.te = te;
        this.pinv = inv;
        craftMatrix = te.getCraftingInv(inv.player);
        craftResult = te.getCraftResult(inv.player);
        init();
        te.registerCrafting(this);
    }

    public CraftingTerminalMenu(int id, Inventory inv) {
        super(MenuRegistry.STORAGE.get(), id);
        this.pinv = inv;
        craftMatrix = new TransientCraftingContainer(this, 3, 3);
        craftResult = new ResultContainer();
        init();
    }

    @Override
    public void removed(Player playerIn) {
        super.removed(playerIn);
        if (te != null) {
            te.unregisterCrafting(this);
        }
    }

    private void init() {
        int x = -4;
        int y = 70;
        this.addSlot(craftingResultSlot = new ActiveResultSlot(pinv.player, craftMatrix, craftResult, 0, x + 130, y + 37) {
            @Override
            public void onTake(Player thePlayer, ItemStack stack) {
                if (thePlayer.level.isClientSide)
                    return;
                this.checkTakeAchievements(stack);
                if (!pinv.player.getCommandSenderWorld().isClientSide) {
                    te.craft((ServerPlayer) thePlayer, tabs.get(thePlayer.getUUID()));
                }
            }
        });
        if (craftMatrix != null) {
            for (int i = 0; i < 3; ++i) {
                for (int j = 0; j < 3; ++j) {
                    SlotCrafting slot = new SlotCrafting(craftMatrix, j + i * 3, x + 36 + j * 18, 89 + i * 18);
                    this.addSlot(slot);
                    this.craftSlotList.add(slot);
                }
            }
        }
        addStorageSlots(false);
        this.playerSlotsStart = slots.size() - 1;
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                addSlot(new Slot(pinv, j + i * 9 + 9, 13 + j * 18, 157 + i * 18));
            }
        }

        for (int i = 0; i < 9; ++i) {
            addSlot(new Slot(pinv, i, 13 + i * 18, 215));
        }
    }

    public void addStorageSlots(boolean expanded) {
        int lines = 7;
        boolean shouldAdd = storageSlotList.isEmpty();
        for (int i = 0; i < lines; ++i) {
            for (int j = 0; j < 9; ++j) {
                int index = i * 9 + j;
                if (shouldAdd) {
                    storageSlotList.add(new SlotStorage(this.te, index, 13 + j * 18, 21 + i * 18, expanded || i < 3));
                } else {
                    storageSlotList.get(index).show = expanded || i < 3;
                }
            }
        }
        for (SlotCrafting slot : craftSlotList) {
            slot.active = !expanded;
        }
        craftingResultSlot.active = !expanded;
    }

    @Override
    public boolean stillValid(Player playerIn) {
        return te != null && te.canInteractWith(playerIn);
    }

    public final void setSlotContents(int id, StoredItemStack stack) {
        storageSlotList.get(id).setStack(stack);
    }

    public final SlotStorage getSlotByID(int id) {
        return storageSlotList.get(id);
    }

    @Override
    public void broadcastChanges() {
        if (te == null) {
            return;
        }

        Map<StoredItemStack, Long> itemsCount = te.getStacks(tabs.get(pinv.player.getUUID()));
        List<StoredItemStack> toWrite = new ArrayList<>();
        Set<StoredItemStack> found = new HashSet<>();
        itemsCount.forEach((s, c) -> {
            long pc = this.itemLongMap.getLong(s);
            if (pc != 0L) found.add(s);
            if (pc != c) {
                toWrite.add(new StoredItemStack(s.getStack(), c));
            }
        });
        this.itemLongMap.forEach((s, c) -> {
            if (!found.contains(s))
                toWrite.add(new StoredItemStack(s.getStack(), 0L));
        });
        this.itemLongMap.clear();
        this.itemLongMap.putAll(itemsCount);
        if (!toWrite.isEmpty()) {
            Networking.sendToPlayerClient(new UpdateStorageItemsPacket(toWrite), (ServerPlayer) pinv.player);
        }

        if (!sentSettings) {
            Networking.sendToPlayerClient(new SetTerminalSettingsPacket(te.sortSettings, null), (ServerPlayer) pinv.player);
            Networking.sendToPlayerClient(new ServerToClientStoragePacket(te.getLastSearch(pinv.player), te.getTabNames()), (ServerPlayer) pinv.player);
            sentSettings = true;
        }

        super.broadcastChanges();
    }

    @Override
    public final ItemStack quickMoveStack(Player playerIn, int index) {
        if (slots.size() <= index)
            return ItemStack.EMPTY;

        if (index > playerSlotsStart && te != null) {
            if (slots.get(index) != null && slots.get(index).hasItem()) {
                Slot slot = slots.get(index);
                ItemStack slotStack = slot.getItem();
                StoredItemStack c = te.pushStack(new StoredItemStack(slotStack, slotStack.getCount()), tabs.get(playerIn.getUUID()));
                ItemStack itemstack = c != null ? c.getActualStack() : ItemStack.EMPTY;
                slot.set(itemstack);
                if (!playerIn.level.isClientSide)
                    broadcastChanges();
            }
        } else if (playerIn instanceof ServerPlayer serverPlayer) {
            Slot slot = this.slots.get(index);
            if (slot != null && slot.hasItem()) {
                ItemStack itemstack1 = slot.getItem();
                ItemStack itemstack = itemstack1.copy();
                if (index == 0) {
                    if (te == null) return ItemStack.EMPTY;
                    te.craftShift(serverPlayer, tabs.get(serverPlayer.getUUID()));
                    if (!serverPlayer.level.isClientSide)
                        broadcastChanges();
                    return ItemStack.EMPTY;
                } else if (index > 0 && index < 10) {
                    if (te == null) return ItemStack.EMPTY;
                    ItemStack stack = te.pushStack(itemstack, tabs.get(serverPlayer.getUUID()));
                    slot.set(stack);
                    if (!serverPlayer.level.isClientSide)
                        broadcastChanges();
                }
                slot.onTake(serverPlayer, itemstack1);
            }
        }

        return ItemStack.EMPTY;
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack stack, Slot slotIn) {
        return slotIn.container != craftResult && super.canTakeItemForPickAll(stack, slotIn);
    }

    public void onCraftMatrixChanged() {
        for (int i = 0; i < slots.size(); ++i) {
            Slot slot = slots.get(i);

            if (slot instanceof SlotCrafting || slot == craftingResultSlot) {
                for (ContainerListener listener : listeners) {
                    if (listener instanceof ServerPlayer) {
                        ((ServerPlayer) listener).connection.send(new ClientboundContainerSetSlotPacket(containerId, incrementStateId(), i, slot.getItem()));
                    }
                }
            }
        }
    }

    @Override
    public boolean clickMenuButton(Player playerIn, int id) {
        if (te != null && id == 0)
            te.clear(playerIn, tabs.get(playerIn.getUUID()));
        else super.clickMenuButton(playerIn, id);
        return false;
    }

    @Override
    public void fillCraftSlotsStackedContents(StackedContents itemHelperIn) {
        this.craftMatrix.fillStackedContents(itemHelperIn);
    }

    @Override
    public void clearCraftingContent() {
        this.craftMatrix.clearContent();
        this.craftResult.clearContent();
    }

    @Override
    public boolean recipeMatches(RecipeHolder pRecipe) {
        return pRecipe.value().matches(this.craftMatrix.asCraftInput(), this.pinv.player.level);
    }

    @Override
    public int getResultSlotIndex() {
        return 0;
    }

    @Override
    public int getGridWidth() {
        return this.craftMatrix.getWidth();
    }

    @Override
    public int getGridHeight() {
        return this.craftMatrix.getHeight();
    }

    @Override
    public int getSize() {
        return 10;
    }

    @Override
    public boolean shouldMoveToInventory(int slotIndex) {
        return slotIndex != getResultSlotIndex();
    }

    @Override
    public RecipeBookType getRecipeBookType() {
        return RecipeBookType.CRAFTING;
    }

    @Override
    public List<RecipeBookCategories> getRecipeBookCategories() {
        return Lists.newArrayList(RecipeBookCategories.CRAFTING_SEARCH, RecipeBookCategories.CRAFTING_EQUIPMENT, RecipeBookCategories.CRAFTING_BUILDING_BLOCKS, RecipeBookCategories.CRAFTING_MISC, RecipeBookCategories.CRAFTING_REDSTONE);
    }

    public void updateItems(List<StoredItemStack> stacks) {
        if (stacks.isEmpty()) {
            return;
        }
        stacks.forEach(s -> {
            if (s.getQuantity() == 0) {
                this.itemMap.remove(s);
            } else {
                this.itemMap.put(s, s);
            }

        });
        itemList = new ArrayList<>(itemMap.values());
        pinv.setChanged();
    }

    public final void receiveServerSearchString(String searchString) {
        if (!searchString.isEmpty())
            search = searchString;
    }


    public void receiveClientSearch(ServerPlayer sender, String search) {
        te.setLastSearch(sender, search);
    }

    public void receiveSettings(ServerPlayer sender, SortSettings settings, String selectedTab) {
        this.tabs.put(sender.getUUID(), selectedTab);
        te.setSorting(settings);
    }

    public class TerminalRecipeItemHelper extends StackedContents {
        @Override
        public void clear() {
            super.clear();
            if (pinv.player.level.isClientSide) {
                itemList.forEach(stored -> accountStored(stored.getStack(), stored.getQuantity()));
            } else {
                te.getStacks(tabs.get(pinv.player.getUUID())).forEach((stored, count) -> accountStored(stored.getStack(), count));
            }
        }

        private void accountStored(ItemStack stack, long count) {
            if (!stack.isDamaged() && !stack.isEnchanted() && !stack.has(DataComponents.CUSTOM_NAME)) {
                int amount = (int) Math.min(count, getGridWidth() * getGridHeight() * stack.getMaxStackSize());
                accountStack(stack.copyWithCount(amount), amount);
            }
        }
    }

    @Override
    public void handlePlacement(boolean pPlaceAll, RecipeHolder<?> pRecipe, ServerPlayer pPlayer) {
        String tab = tabs.get(pPlayer.getUUID());
        new ServerPlaceRecipe<CraftingInput, CraftingRecipe>(this) {
            {
                stackedContents = new TerminalRecipeItemHelper();
            }

            @Override
            protected int moveItemToGrid(Slot slot, ItemStack stack, int maxAmount) {
                int remaining = super.moveItemToGrid(slot, stack, maxAmount);
                if (remaining != -1) {
                    return remaining;
                }
                StoredItemStack pulled = te.pullStack(new StoredItemStack(stack), maxAmount, tab);
                if (pulled == null) {
                    return -1;
                }
                if (slot.getItem().isEmpty()) {
                    slot.set(pulled.getActualStack());
                } else {
                    slot.getItem().grow((int) pulled.getQuantity());
                }
                return maxAmount - (int) pulled.getQuantity();
            }
        }.recipeClicked(pPlayer, (RecipeHolder<CraftingRecipe>) pRecipe, pPlaceAll);
    }

    public void onTransferHandler(ServerPlayer sender, List<List<ItemStack>> stacksList) {
        ItemStack[][] stacks = new ItemStack[9][];
        for (int i = 0; i < stacksList.size(); i++) {
            stacks[i] = stacksList.get(i).toArray(ItemStack[]::new);
        }

        te.transferToGrid(pinv.player, stacks, tabs.get(sender.getUUID()));
    }

    @Override
    public List<StoredItemStack> getStoredItems() {
        return itemList;
    }

    public void onInteract(ServerPlayer player, @Nullable StoredItemStack clicked, SlotAction act, boolean pullOne) {
        player.resetLastActionTime();
        if (act == SlotAction.SPACE_CLICK) {
            for (int i = playerSlotsStart + 1; i < playerSlotsStart + 28; i++) {
                quickMoveStack(player, i);
            }
        } else {
            String selectedTab = tabs.get(player.getUUID());
            if (act == SlotAction.PULL_OR_PUSH_STACK) {
                ItemStack stack = getCarried();
                if (!stack.isEmpty()) {
                    StoredItemStack rem = te.pushStack(new StoredItemStack(stack), selectedTab);
                    ItemStack itemstack = rem == null ? ItemStack.EMPTY : rem.getActualStack();
                    setCarried(itemstack);
                } else {
                    if (clicked == null) return;
                    StoredItemStack pulled = te.pullStack(clicked, clicked.getMaxStackSize(), selectedTab);
                    if (pulled != null) {
                        setCarried(pulled.getActualStack());
                    }
                }
            } else if (act == SlotAction.PULL_ONE) {
                ItemStack stack = getCarried();
                if (stack.getItem() instanceof BundleItem) {
                    if (clicked != null) {
                        if (!clicked.getStack().canFitInsideContainerItems()) {
                            return;
                        }

                        var bundleContents = stack.get(DataComponents.BUNDLE_CONTENTS);
                        if (bundleContents != null && Fraction.ONE.subtract(bundleContents.weight()).compareTo(BundleContents.getWeight(clicked.getStack().copyWithCount(1))) < 0) {
                            return;
                        }

                        StoredItemStack pulled = te.pullStack(clicked, 1, selectedTab);
                        if (pulled != null) {
                            var actual = pulled.getActualStack();
                            ArrayList<ItemStack> newItems = new ArrayList<>();
                            if (bundleContents != null) {
                                newItems.ensureCapacity(bundleContents.size() + 1);
                                for (var item : bundleContents.items()) {
                                    if (!item.isEmpty()) {
                                        if (!actual.isEmpty() && ItemStack.isSameItemSameComponents(item, actual)) {
                                            var amount = Math.min(actual.getCount(), item.getMaxStackSize() - item.getCount());
                                            item.setCount(item.getCount() + amount);
                                            actual.setCount(actual.getCount() - amount);
                                        }
                                        newItems.add(item);
                                    }
                                }
                            }
                            if (!actual.isEmpty()) {
                                newItems.add(pulled.getActualStack());
                            }

                            player.level.playSound(null, player, SoundEvents.BUNDLE_INSERT, SoundSource.PLAYERS, 0.8F, 0.8F + player.level.getRandom().nextFloat() * 0.4F);
                            stack.set(DataComponents.BUNDLE_CONTENTS, new BundleContents(newItems));
                        }
                    } else {
                        var bundleContents = stack.get(DataComponents.BUNDLE_CONTENTS);
                        if (bundleContents == null) {
                            return;
                        }

                        ArrayList<ItemStack> newItems = new ArrayList<>();
                        for (var item : bundleContents.items()) {
                            item = te.pushStack(item, selectedTab);
                            if (!item.isEmpty()) {
                                newItems.add(item);
                            }
                        }

                        player.level.playSound(null, player, SoundEvents.BUNDLE_DROP_CONTENTS, SoundSource.PLAYERS, 0.8F, 0.8F + player.level.getRandom().nextFloat() * 0.4F);
                        stack.set(DataComponents.BUNDLE_CONTENTS, new BundleContents(newItems));
                    }

                    return;
                }

                if (clicked == null || clicked.getStack() == null || clicked.getQuantity() <= 0) {
                    return;
                }

                if (pullOne) {
                    StoredItemStack pulled = te.pullStack(clicked, 1, selectedTab);
                    if (pulled != null) {
                        ItemStack itemstack = pulled.getActualStack();
                        this.moveItemStackTo(itemstack, playerSlotsStart + 1, this.slots.size(), true);
                        if (itemstack.getCount() > 0)
                            te.pushOrDrop(itemstack, selectedTab);
                        player.getInventory().setChanged();
                    }
                } else {
                    if (!stack.isEmpty()) {
                        if (ItemStack.isSameItemSameComponents(stack, clicked.getStack()) && stack.getCount() + 1 <= stack.getMaxStackSize()) {
                            StoredItemStack pulled = te.pullStack(clicked, 1, selectedTab);
                            if (pulled != null) {
                                stack.grow(1);
                            }
                        }
                    } else {
                        StoredItemStack pulled = te.pullStack(clicked, 1, selectedTab);
                        if (pulled != null) {
                            setCarried(pulled.getActualStack());
                        }
                    }
                }
            } else if (act == SlotAction.GET_HALF) {
                ItemStack stack = getCarried();
                if (!stack.isEmpty()) {
                    if (stack.getItem() instanceof BundleItem) {
                        if (clicked != null) {
                            if (!clicked.getStack().canFitInsideContainerItems()) {
                                return;
                            }

                            var bundleContents = stack.get(DataComponents.BUNDLE_CONTENTS);
                            int canFit = clicked.getStack().getMaxStackSize();
                            if (bundleContents != null) {
                                canFit = Fraction.ONE.subtract(bundleContents.weight()).divideBy(BundleContents.getWeight(clicked.getStack().copyWithCount(1))).intValue();
                            }

                            StoredItemStack pulled = te.pullStack(clicked, canFit, selectedTab);
                            if (pulled != null) {
                                var actual = pulled.getActualStack();
                                ArrayList<ItemStack> newItems = new ArrayList<>();
                                if (bundleContents != null) {
                                    newItems.ensureCapacity(bundleContents.size() + 1);
                                    for (var item : bundleContents.items()) {
                                        if (!item.isEmpty()) {
                                            if (!actual.isEmpty() && ItemStack.isSameItemSameComponents(item, actual)) {
                                                var amount = Math.min(actual.getCount(), item.getMaxStackSize() - item.getCount());
                                                item.setCount(item.getCount() + amount);
                                                actual.setCount(actual.getCount() - amount);
                                            }
                                            newItems.add(item);
                                        }
                                    }
                                }
                                if (!actual.isEmpty()) {
                                    newItems.add(pulled.getActualStack());
                                }

                                player.level.playSound(null, player, SoundEvents.BUNDLE_INSERT, SoundSource.PLAYERS, 0.8F, 0.8F + player.level.getRandom().nextFloat() * 0.4F);
                                stack.set(DataComponents.BUNDLE_CONTENTS, new BundleContents(newItems));
                            }
                        } else {
                            var bundleContents = stack.get(DataComponents.BUNDLE_CONTENTS);
                            if (bundleContents == null) {
                                return;
                            }

                            var pushed = false;
                            ArrayList<ItemStack> newItems = new ArrayList<>();
                            for (var item : bundleContents.items()) {
                                if (!pushed && !item.isEmpty()) {
                                    item = te.pushStack(item, selectedTab);
                                    if (!item.isEmpty()) {
                                        newItems.add(item);
                                    }
                                    player.level.playSound(null, player, SoundEvents.BUNDLE_DROP_CONTENTS, SoundSource.PLAYERS, 0.8F, 0.8F + player.level.getRandom().nextFloat() * 0.4F);
                                    pushed = true;
                                    continue;
                                }

                                newItems.add(item);
                            }

                            stack.set(DataComponents.BUNDLE_CONTENTS, new BundleContents(newItems));
                        }

                        return;
                    }
                    ItemStack stack1 = stack.split(Math.max(Math.min(stack.getCount(), stack.getMaxStackSize()) / 2, 1));
                    ItemStack itemstack = te.pushStack(stack1, selectedTab);
                    stack.grow(!itemstack.isEmpty() ? itemstack.getCount() : 0);
                    setCarried(stack);
                } else {
                    if (clicked == null) {
                        return;
                    }
                    StoredItemStack pulled = te.pullStack(clicked, (int) Math.max(Math.min(clicked.getQuantity() / 2, clicked.getMaxStackSize() / 2), 1), selectedTab);
                    if (pulled != null) {
                        setCarried(pulled.getActualStack());
                    }
                }
            } else if (act == SlotAction.GET_QUARTER) {
                ItemStack stack = getCarried();
                if (!stack.isEmpty()) {
                    ItemStack stack1 = stack.split(Math.max(Math.min(stack.getCount(), stack.getMaxStackSize()) / 4, 1));
                    ItemStack itemstack = te.pushStack(stack1, selectedTab);
                    stack.grow(!itemstack.isEmpty() ? itemstack.getCount() : 0);
                    setCarried(stack);
                } else {
                    if (clicked == null) return;
                    long maxCount = 64;
                    for (StoredItemStack e : itemList) {
                        if (e.equals(clicked)) maxCount = e.getQuantity();
                    }
                    StoredItemStack pulled = te.pullStack(clicked, (int) Math.max(Math.min(maxCount, clicked.getMaxStackSize()) / 4, 1), selectedTab);
                    if (pulled != null) {
                        setCarried(pulled.getActualStack());
                    }
                }
            } else {
                if (clicked == null) return;
                StoredItemStack pulled = te.pullStack(clicked, clicked.getMaxStackSize(), selectedTab);
                if (pulled != null) {
                    ItemStack itemstack = pulled.getActualStack();
                    this.moveItemStackTo(itemstack, playerSlotsStart + 1, this.slots.size(), true);
                    if (itemstack.getCount() > 0) {
                        te.pushOrDrop(itemstack, selectedTab);
                    }
                    player.getInventory().setChanged();
                }
            }
        }
    }
}
