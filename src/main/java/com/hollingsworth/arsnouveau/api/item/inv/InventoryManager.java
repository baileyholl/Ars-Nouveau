package com.hollingsworth.arsnouveau.api.item.inv;

import com.hollingsworth.arsnouveau.api.spell.wrapped_caster.IWrappedCaster;
import com.hollingsworth.arsnouveau.api.util.InvUtil;
import com.hollingsworth.arsnouveau.common.items.ItemScroll;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Stores a list of {@link FilterableItemHandler}s and provides methods to interact with them.
 */
public class InventoryManager {

    private static final Random random = new Random();

    public List<FilterableItemHandler> filterables;

    private int extractSlotMax = -1;

    private int insertSlotMax = -1;

    public InventoryManager() {
        this(new ArrayList<>());
    }

    public InventoryManager(List<FilterableItemHandler> filterables) {
        this.filterables = filterables;
    }

    public InventoryManager(IWrappedCaster wrappedCaster) {
        this(wrappedCaster.getInventory());
    }

    public static InventoryManager fromTile(BlockEntity blockEntity) {
        return new InventoryManager(InvUtil.adjacentInventories(blockEntity.getLevel(), blockEntity.getBlockPos()));
    }

    public InventoryManager extractSlotMax(int slotMax) {
        this.extractSlotMax = slotMax;
        return this;
    }

    public InventoryManager insertSlotMax(int slotMax) {
        this.insertSlotMax = slotMax;
        return this;
    }

    public List<FilterableItemHandler> getInventory() {
        return filterables;
    }

    /**
     * Inserts a stack into all valid inventories, sorted by preference.
     * Drops the remainder on the ground at the given position.
     */
    public void insertOrDrop(ItemStack stack, Level level, BlockPos pos) {
        ItemStack remainder = insertStack(stack);
        if (!remainder.isEmpty()) {
            level.addFreshEntity(new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(), remainder.copy()));
            remainder.setCount(0);
        }
    }

    /**
     * Inserts a stack into all valid inventories, sorted by preference.
     *
     * @return Remaining or empty stack.
     */
    public ItemStack insertStack(ItemStack stack) {
        return insertStackWithReference(stack).getRemainder();
    }

    public MultiInsertReference insertStackWithReference(ItemStack stack) {
        List<SlotReference> references = new ArrayList<>();
        for (FilterableItemHandler filterable : preferredForStack(stack, false)) {
            int count = stack.getCount();
            stack = filterable.insertItemStacked(stack, false);
            if (count != stack.getCount()) {
                references.add(new SlotReference(filterable.getHandler(), filterable.getHandler().getSlots()));
            }
            if (stack.isEmpty()) {
                break;
            }
        }
        return new MultiInsertReference(stack, references);
    }

    /**
     * Extracts a stack from the highest preferred inventory that contains items that match the predicate.
     *
     * @param predicate The predicate to match items against.
     */
    public ExtractedStack extractItem(Predicate<ItemStack> predicate, int count) {
        FilterableItemHandler highestHandler = highestPrefInventory(getInventory(), predicate, InteractType.EXTRACT);
        return highestHandler == null ? ExtractedStack.empty() : extractItem(highestHandler, predicate, count);
    }

    public ExtractedStack extractItem(FilterableItemHandler filteredHandler, Predicate<ItemStack> stackPredicate, int count) {
        SlotReference slotRef = findItem(filteredHandler, stackPredicate, InteractType.EXTRACT);
        return slotRef.isEmpty() ? ExtractedStack.empty() : ExtractedStack.from(slotRef, count);
    }

    /**
     * Extracts a stack from the highest preferred inventory that contains items that match the predicate.
     *
     * @param predicate The predicate to match items against.
     */
    public ExtractedStack extractRandomItem(Predicate<ItemStack> predicate, int count) {
        FilterableItemHandler highestHandler = highestPrefInventory(getInventory(), predicate, InteractType.EXTRACT);
        return highestHandler == null ? ExtractedStack.empty() : extractRandomItem(highestHandler, predicate, count);
    }

    public ExtractedStack extractRandomItem(FilterableItemHandler filteredHandler, Predicate<ItemStack> stackPredicate, int count) {
        SlotReference slotRef = findItemR(filteredHandler, stackPredicate, InteractType.EXTRACT);
        return slotRef.isEmpty() ? ExtractedStack.empty() : ExtractedStack.from(slotRef, count);
    }

    /**
     * Continuously extracts from the handler until the desired stack and size is extracted or the handler is empty.
     */
    public MultiExtractedReference extractAllFromHandler(FilterableItemHandler filterableItemHandler, ItemStack desiredStack, int count) {
        ItemStack merged = ItemStack.EMPTY;
        int remaining = Math.min(desiredStack.getMaxStackSize(), count);
        List<ExtractedStack> extractedStacks = new ArrayList<>();
        IItemHandler itemHandler = filterableItemHandler.getHandler();
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            ItemStack stack = itemHandler.extractItem(i, remaining, true);
            if(stack.isEmpty()){
                continue;
            }
            if (!(ItemStack.isSameItem(stack, desiredStack) && ItemStack.isSameItemSameComponents(stack, desiredStack))) {
                continue;
            }
            int toExtract = Math.min(stack.getCount(), remaining);
            remaining -= toExtract;
            if (merged.isEmpty()) {
                merged = stack.copy();
                merged.setCount(toExtract);
            } else {
                merged.grow(toExtract);
            }
            // actually extracts the stack
            extractedStacks.add(ExtractedStack.from(filterableItemHandler.getHandler(), i, toExtract));
            if (remaining <= 0)
                break;
        }
        return new MultiExtractedReference(merged, extractedStacks);
    }

    /**
     * Checks all inventories for the desired stack and extracts the desired amount from all of them until the desired amount is reached.
     */
    public MultiExtractedReference extractItemFromAll(ItemStack desiredStack, int count, boolean includeInvalidInvs) {
        ItemStack merged = ItemStack.EMPTY;
        int remaining = count;
        List<FilterableItemHandler> preferred = preferredForStack(desiredStack, includeInvalidInvs);
        List<ExtractedStack> extracted = new ArrayList<>();
        for (FilterableItemHandler filterable : preferred) {
            if (remaining <= 0)
                break;
            MultiExtractedReference extractedFromHandler = extractAllFromHandler(filterable, desiredStack, remaining);
            if (extractedFromHandler.isEmpty())
                continue;

            remaining -= extractedFromHandler.extracted.getCount();
            if (merged.isEmpty()) {
                merged = extractedFromHandler.extracted;
            } else {
                merged.grow(extractedFromHandler.extracted.getCount());
            }
            extracted.addAll(extractedFromHandler.slots);

        }
        return new MultiExtractedReference(merged, extracted);
    }

    /**
     * Returns a reference to a matching stack, if any. Does not modify the inventory.
     */
    public SlotReference findItem(Predicate<ItemStack> predicate, InteractType type) {
        FilterableItemHandler highestHandler = highestPrefInventory(getInventory(), predicate, type);
        if (highestHandler == null) {
            return SlotReference.empty();
        }
        return findItem(highestHandler, predicate, type);
    }

    /**
     * Returns a reference to a matching stack, if any. Does not modify the inventory.
     */
    public SlotReference findItem(FilterableItemHandler itemHandler, Predicate<ItemStack> stackPredicate, InteractType type) {
        for (int slot = 0; slot < maxSlotForType(itemHandler, type); slot++) {
            ItemStack stackInSlot = itemHandler.getHandler().getStackInSlot(slot);
            if (!stackInSlot.isEmpty() && stackPredicate.test(stackInSlot) && itemHandler.canInteractFor(stackInSlot, type).valid()) {
                return new SlotReference(itemHandler.getHandler(), slot);
            }
        }
        return SlotReference.empty();
    }

    /**
     * Returns a reference to a random matching stack, if any. Does not modify the inventory. Chance is a percentage to choose the stack.
     */
    public SlotReference findItemR(FilterableItemHandler itemHandler, Predicate<ItemStack> stackPredicate, InteractType type) {
        List<Integer> validSlots = new ArrayList<>();
        //filter the valid slots
        for (int slot = 0; slot < maxSlotForType(itemHandler, type); slot++) {
            ItemStack stackInSlot = itemHandler.getHandler().getStackInSlot(slot);
            if (!stackInSlot.isEmpty() && stackPredicate.test(stackInSlot) && itemHandler.canInteractFor(stackInSlot, type).valid()) {
                validSlots.add(slot);
            }
        }
        if(validSlots.isEmpty()){
            return SlotReference.empty();
        }
        //apply uniform chance if there are any valid slots
        return new SlotReference(itemHandler.getHandler(), validSlots.get(random.nextInt(validSlots.size())));
    }

    /**
     * Returns the sorted list of highest preferred inventories for a given stack based on their list of filters.
     *
     * @return The list of inventories sorted by highest preference.
     */
    public List<FilterableItemHandler> preferredForStack(ItemStack stack, boolean includeInvalid) {
        List<FilterableItemHandler> filtered = new ArrayList<>(getInventory());
        filtered = filtered.stream()
                .filter(filterableItemHandler -> includeInvalid || filterableItemHandler.getHighestPreference(stack) != ItemScroll.SortPref.INVALID)
                .collect(Collectors.toCollection(ArrayList::new));
        /// Sort by highest pref first
        filtered.sort((o1, o2) -> o2.getHighestPreference(stack).ordinal() - o1.getHighestPreference(stack).ordinal());
        return filtered;
    }

    public FilterableItemHandler highestPrefInventory(List<FilterableItemHandler> inventories, Predicate<ItemStack> predicate, InteractType type) {
        ItemScroll.SortPref highestPref = ItemScroll.SortPref.INVALID;
        FilterableItemHandler highestHandler = null;
        for (FilterableItemHandler wrapper : inventories) {
            ItemScroll.SortPref pref = ItemScroll.SortPref.LOW;
            // Get the highest pref item in the handler
            for (int i = 0; i < maxSlotForType(wrapper, type); i++) {
                // Simulate extract to respect modded inventory filters
                ItemStack stack = wrapper.getHandler().extractItem(i, 1, true);
                if (stack.isEmpty() || !predicate.test(stack))
                    continue;
                InteractResult result = wrapper.canInteractFor(stack, type);
                ItemScroll.SortPref foundPref = result.sortPref();
                if (!result.valid()) {
                    continue;
                }
                if (foundPref.ordinal() > pref.ordinal()) {
                    pref = foundPref;
                }
                if (pref == ItemScroll.SortPref.HIGHEST)
                    return wrapper;
            }
            if (pref.ordinal() > highestPref.ordinal()) {
                highestHandler = wrapper;
                highestPref = pref;
            }
        }
        return highestHandler;
    }

    private int maxSlotForType(FilterableItemHandler filterableItemHandler, InteractType interactType) {
        if (interactType == InteractType.EXTRACT) {
            return getExtractSlotMax(filterableItemHandler);
        } else {
            return getInsertSlotMax(filterableItemHandler);
        }
    }

    private int getExtractSlotMax(FilterableItemHandler handler) {
        if (extractSlotMax == -1)
            return handler.getHandler().getSlots();
        return Math.min(extractSlotMax, handler.getHandler().getSlots());
    }

    private int getInsertSlotMax(FilterableItemHandler handler) {
        if (insertSlotMax == -1)
            return handler.getHandler().getSlots();
        return Math.min(insertSlotMax, handler.getHandler().getSlots());
    }
}
