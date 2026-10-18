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

    protected static int playerSlotsStart = 10;

    protected CraftingLecternTile te;
    protected List<SlotStorage> storageSlotList = new ArrayList<>();
    public List<StoredItemStack> itemList = new ArrayList<>();
    protected Inventory pinv;
    public String search;
    public Map<StoredItemStack, StoredItemStack> itemMap = new HashMap<>();
    boolean sentSettings = false;
    private String selectedTab;
    private Object2LongMap<StoredItemStack> itemLongMap = new Object2LongOpenHashMap<>();
    protected List<SlotCrafting> craftSlotList = new ArrayList<>();
    private final CraftingContainer craftMatrix;
    private final ResultContainer craftResult;
    private ActiveResultSlot craftingResultSlot;

    public CraftingTerminalMenu(int id, Inventory inv, CraftingLecternTile te) {
        super(MenuRegistry.STORAGE.get(), id);
        this.te = te;
        this.pinv = inv;
        craftMatrix = te.getCraftingInv(inv.player);
        craftResult = te.getCraftResult(inv.player);
        init();
    }

    public CraftingTerminalMenu(int id, Inventory inv) {
        super(MenuRegistry.STORAGE.get(), id);
        this.pinv = inv;
        craftMatrix = new TransientCraftingContainer(this, 3, 3);
        craftResult = new ResultContainer();
        init();
    }

    private void init() {
        int x = -4;
        int y = 70;
        this.addSlot(craftingResultSlot = new ActiveResultSlot(pinv.player, craftMatrix, craftResult, 0, x + 130, y + 37) {
            @Override
            public void onTake(Player thePlayer, ItemStack stack) {
                if (thePlayer instanceof ServerPlayer serverPlayer) {
                    this.checkTakeAchievements(stack);
                    te.craft(serverPlayer, selectedTab);
                }
            }
        });
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                SlotCrafting slot = new SlotCrafting(craftMatrix, j + i * 3, x + 36 + j * 18, 89 + i * 18);
                this.addSlot(slot);
                this.craftSlotList.add(slot);
            }
        }
        for (int i = 0; i < 7; ++i) {
            for (int j = 0; j < 9; ++j) {
                storageSlotList.add(new SlotStorage(13 + j * 18, 21 + i * 18, i < 3));
            }
        }
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                addSlot(new Slot(pinv, j + i * 9 + 9, 13 + j * 18, 157 + i * 18));
            }
        }

        for (int i = 0; i < 9; ++i) {
            addSlot(new Slot(pinv, i, 13 + i * 18, 215));
        }
    }

    public void setExpanded(boolean expanded) {
        for (int i = 0; i < storageSlotList.size(); i++) {
            storageSlotList.get(i).show = expanded || i < 27;
        }
        for (SlotCrafting slot : craftSlotList) {
            slot.active = !expanded;
        }
        craftingResultSlot.active = !expanded;
    }

    @Override
    public boolean stillValid(Player playerIn) {
        return te != null && !te.isRemoved();
    }

    @Override
    public void broadcastChanges() {
        if (!(pinv.player instanceof ServerPlayer player)) {
            return;
        }

        Map<StoredItemStack, Long> itemsCount = te.getStacks(selectedTab);
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
            Networking.sendToPlayerClient(new UpdateStorageItemsPacket(toWrite), player);
        }

        if (!sentSettings) {
            Networking.sendToPlayerClient(new SetTerminalSettingsPacket(te.sortSettings, null), player);
            Networking.sendToPlayerClient(new ServerToClientStoragePacket(te.getLastSearch(player), te.getTabNames()), player);
            sentSettings = true;
        }

        super.broadcastChanges();
    }

    @Override
    public final ItemStack quickMoveStack(Player playerIn, int index) {
        if (!(playerIn instanceof ServerPlayer serverPlayer) || index >= slots.size() || !slots.get(index).hasItem())
            return ItemStack.EMPTY;

        if (index == getResultSlotIndex()) {
            te.craftShift(serverPlayer, selectedTab);
        } else {
            Slot slot = slots.get(index);
            slot.set(te.pushStack(slot.getItem(), selectedTab));
        }
        broadcastChanges();
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack stack, Slot slotIn) {
        return slotIn.container != craftResult && super.canTakeItemForPickAll(stack, slotIn);
    }

    @Override
    public boolean clickMenuButton(Player playerIn, int id) {
        if (te != null && id == 0)
            te.clear(playerIn, selectedTab);
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


    public void receiveClientSearch(String search) {
        te.setLastSearch(pinv.player, search);
    }

    public void receiveSettings(SortSettings settings, String selectedTab) {
        this.selectedTab = selectedTab;
        te.setSorting(settings);
    }

    public class TerminalRecipeItemHelper extends StackedContents {
        @Override
        public void clear() {
            super.clear();
            if (pinv.player.level.isClientSide) {
                itemList.forEach(stored -> accountStored(stored.getStack(), stored.getQuantity()));
            } else {
                te.getStacks(selectedTab).forEach((stored, count) -> accountStored(stored.getStack(), count));
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
                StoredItemStack pulled = te.pullStack(new StoredItemStack(stack), maxAmount, selectedTab);
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

    public void onTransferHandler(List<List<ItemStack>> stacksList) {
        ItemStack[][] stacks = new ItemStack[9][];
        for (int i = 0; i < stacksList.size(); i++) {
            stacks[i] = stacksList.get(i).toArray(ItemStack[]::new);
        }

        te.transferToGrid(pinv.player, stacks, selectedTab);
    }

    @Override
    public List<StoredItemStack> getStoredItems() {
        return itemList;
    }

    public void onInteract(ServerPlayer player, @Nullable StoredItemStack clicked, SlotAction act, boolean pullOne) {
        player.resetLastActionTime();
        if (act == SlotAction.SPACE_CLICK) {
            for (int i = playerSlotsStart; i < playerSlotsStart + 27; i++) {
                quickMoveStack(player, i);
            }
        } else {
            if (act == SlotAction.PULL_OR_PUSH_STACK) {
                ItemStack stack = getCarried();
                if (!stack.isEmpty()) {
                    setCarried(te.pushStack(stack, selectedTab));
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
                        this.moveItemStackTo(itemstack, playerSlotsStart, this.slots.size(), true);
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
                    this.moveItemStackTo(itemstack, playerSlotsStart, this.slots.size(), true);
                    if (itemstack.getCount() > 0) {
                        te.pushOrDrop(itemstack, selectedTab);
                    }
                    player.getInventory().setChanged();
                }
            }
        }
    }
}
