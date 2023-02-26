package com.hollingsworth.arsnouveau.api.item.inv;

import com.hollingsworth.arsnouveau.api.spell.wrapped_caster.IWrappedCaster;
import com.hollingsworth.arsnouveau.api.util.InvUtil;
import com.hollingsworth.arsnouveau.common.items.ItemScroll;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

/**
 * Stores a list of {@link FilterableItemHandler}s and provides methods to interact with them.
 *
 */
public class InventoryManager {

    private List<FilterableItemHandler> filterables;

    private int extractSlotMax = -1;

    private int insertSlotMax = -1;

    public InventoryManager(List<FilterableItemHandler> filterables){
        this.filterables = filterables;
    }

    public InventoryManager(IWrappedCaster wrappedCaster){
        this(wrappedCaster.getInventory());
    }

    public static InventoryManager fromTile(BlockEntity blockEntity){
        return new InventoryManager(InvUtil.adjacentInventories(blockEntity.getLevel(), blockEntity.getBlockPos()));
    }

    public InventoryManager extractSlotMax(int slotMax){
        this.extractSlotMax = slotMax;
        return this;
    }

    public InventoryManager insertSlotMax(int slotMax){
        this.insertSlotMax = slotMax;
        return this;
    }

    public boolean addFilterable(FilterableItemHandler filterable){
        return this.filterables.add(filterable);
    }

    public List<FilterableItemHandler> getInventory(){
        return filterables;
    }

    /**
     * Inserts a stack into all valid inventories, sorted by preference.
     * Drops the remainder on the ground at the given position.
     */
    public void insertOrDrop(ItemStack stack, Level level, BlockPos pos){
        ItemStack remainder = insertStack(stack);
        if(!remainder.isEmpty()){
            level.addFreshEntity(new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(), remainder.copy()));
            remainder.setCount(0);
        }
    }

    /**
     * Inserts a stack into all valid inventories, sorted by preference.
     * @return Remaining or empty stack.
     */
    public ItemStack insertStack(ItemStack stack){
        for(FilterableItemHandler filterable : preferredForStack(stack)){
            stack = ItemHandlerHelper.insertItemStacked(filterable.getHandler(), stack, false);
            if(stack.isEmpty())
                return ItemStack.EMPTY;
        }
        return stack;
    }

    public ExtractedStack extractByAmount(ToIntFunction<ItemStack> getExtractAmount){
        ItemScroll.SortPref highestPref = ItemScroll.SortPref.INVALID;
        FilterableItemHandler highestHandler = null;
        int toExtract = 0;
        int slot = -1;
        for(FilterableItemHandler wrapper : getInventory()){
            ItemScroll.SortPref pref = ItemScroll.SortPref.INVALID;
            // Get the highest pref item in the handler
            int forAmount = 0;
            int forSlot = 0;
            for(int i = 0; i < getExtractSlotMax(wrapper); i++){
                ItemStack stack = wrapper.getHandler().getStackInSlot(i);
                if(stack.isEmpty()) {
                    continue;
                }
                int amount = getExtractAmount.applyAsInt(stack);
                if(amount <= 0)
                    continue;
                ItemScroll.SortPref foundPref = wrapper.getHighestPreference(stack);
                if(pref == ItemScroll.SortPref.HIGHEST) {
                    return extractItem(wrapper, stack1 -> true, amount);
                }else if(foundPref == ItemScroll.SortPref.INVALID){
                    continue;
                }
                if(foundPref.ordinal() > pref.ordinal()){
                    pref = foundPref;
                    forAmount = amount;
                    forSlot = i;
                }
            }
            if(pref.ordinal() > highestPref.ordinal()){
                highestHandler = wrapper;
                highestPref = pref;
                toExtract = forAmount;
                slot = forSlot;
            }
        }
        return highestHandler == null ? ExtractedStack.empty() : ExtractedStack.from(highestHandler.getHandler(), slot, toExtract);
    }

    /**
     * Extracts a stack from the highest preferred inventory that contains items that match the predicate.
     * @param predicate The predicate to match items against.
     */
    public ExtractedStack extractItem(Predicate<ItemStack> predicate, int count){
        FilterableItemHandler highestHandler = highestPrefInventory(getInventory(), predicate, InteractType.EXTRACT);
        return highestHandler == null ? ExtractedStack.empty() : extractItem(highestHandler, predicate, count);
    }

    public ExtractedStack extractItem(FilterableItemHandler filteredHandler, Predicate<ItemStack> stackPredicate, int count){
        SlotReference slotRef = findItem(filteredHandler, stackPredicate, InteractType.EXTRACT);
        return slotRef.isEmpty() ? ExtractedStack.empty() : ExtractedStack.from(slotRef, count);
    }

    public MultiExtractedReference extractItemFromAll(ItemStack desiredStack, int count){
        ItemStack merged = ItemStack.EMPTY;
        int remaining = count;
        List<FilterableItemHandler> preferred = preferredForStack(desiredStack);
        List<ExtractedStack> extracted = new ArrayList<>();
        for(FilterableItemHandler filterable : preferred){
            if(remaining <= 0)
                return new MultiExtractedReference(merged, extracted);
            ExtractedStack extractedFromHandler = extractItem(filterable, stack ->ItemStack.isSame(stack, desiredStack) && ItemStack.tagMatches(stack, desiredStack), remaining);
            if(extractedFromHandler.isEmpty())
                continue;
            remaining -= extractedFromHandler.getStack().getCount();
            if(merged.isEmpty()){
                merged = extractedFromHandler.getStack();
            }else{
                merged.grow(extractedFromHandler.getStack().getCount());
            }
            extracted.add(extractedFromHandler);
        }
        return new MultiExtractedReference(merged, extracted);
    }

    /**
     * Returns a reference to a matching stack, if any. Does not modify the inventory.
     */
    public SlotReference findItem(Predicate<ItemStack> predicate, InteractType type){
        FilterableItemHandler highestHandler = highestPrefInventory(getInventory(), predicate, type);
        if(highestHandler == null){
            return SlotReference.empty();
        }
        return findItem(highestHandler, predicate, type);
    }

    /**
     * Returns a reference to a matching stack, if any. Does not modify the inventory.
     */
    public SlotReference findItem(FilterableItemHandler itemHandler, Predicate<ItemStack> stackPredicate, InteractType type){
        for(int slot = 0; slot < maxSlotForType(itemHandler, type); slot++){
            ItemStack stackInSlot = itemHandler.getHandler().getStackInSlot(slot);
            if(!stackInSlot.isEmpty() && stackPredicate.test(stackInSlot) && itemHandler.canInteractFor(stackInSlot, type)){
                return new SlotReference(itemHandler.getHandler(), slot);
            }
        }
        return SlotReference.empty();
    }

    /**
     * Returns a list with up to maxSlots references to matching stacks, if any. Does not modify the inventory.
     */
    public List<SlotReference> findItems(FilterableItemHandler itemHandler, Predicate<ItemStack> stackPredicate, InteractType type, int maxSlots){
        List<SlotReference> slots = new ArrayList<>();
        int numSlots = Math.min(maxSlotForType(itemHandler, type), maxSlots);
        for(int slot = 0; slot < numSlots; slot++){
            ItemStack stackInSlot = itemHandler.getHandler().getStackInSlot(slot);
            if(!stackInSlot.isEmpty() && stackPredicate.test(stackInSlot) && itemHandler.canInteractFor(stackInSlot, type)){
                slots.add(new SlotReference(itemHandler.getHandler(), slot));
            }
        }
        return slots;
    }

    /**
     * Returns the sorted list of highest preferred inventories for a given stack based on their list of filters.
     * @return The list of inventories sorted by highest preference.
     */
    public List<FilterableItemHandler> preferredForStack(ItemStack stack){
        List<FilterableItemHandler> filtered = new ArrayList<>(getInventory());
        filtered = filtered.stream().filter(filterableItemHandler -> filterableItemHandler.getHighestPreference(stack) != ItemScroll.SortPref.INVALID).collect(Collectors.toCollection(ArrayList::new));
        /// Sort by highest pref first
        filtered.sort((o1, o2) -> o2.getHighestPreference(stack).ordinal() - o1.getHighestPreference(stack).ordinal());
        return filtered;
    }

    public FilterableItemHandler highestPrefInventory(List<FilterableItemHandler> inventories, Predicate<ItemStack> predicate, InteractType type){
        ItemScroll.SortPref highestPref = ItemScroll.SortPref.INVALID;
        FilterableItemHandler highestHandler = null;
        for(FilterableItemHandler wrapper : inventories){
            ItemScroll.SortPref pref = ItemScroll.SortPref.LOW;
            // Get the highest pref item in the handler
            for(int i = 0; i < maxSlotForType(wrapper, type); i++){
                ItemStack stack = wrapper.getHandler().getStackInSlot(i);
                if(stack.isEmpty() || !predicate.test(stack))
                    continue;
                ItemScroll.SortPref foundPref = wrapper.getHighestPreference(stack);
                if(foundPref == ItemScroll.SortPref.INVALID){
                    continue;
                }
                if(foundPref.ordinal() > pref.ordinal()){
                    pref = foundPref;
                }
                if(pref == ItemScroll.SortPref.HIGHEST)
                    return wrapper;
            }
            if(pref.ordinal() > highestPref.ordinal()){
                highestHandler = wrapper;
                highestPref = pref;
            }
        }
        return highestHandler;
    }

    private int maxSlotForType(FilterableItemHandler filterableItemHandler, InteractType interactType){
        if(interactType == InteractType.EXTRACT){
            return getExtractSlotMax(filterableItemHandler);
        }else{
            return getInsertSlotMax(filterableItemHandler);
        }
    }

    private int getExtractSlotMax(FilterableItemHandler handler){
        if(extractSlotMax == -1)
            return handler.getHandler().getSlots();
        return Math.min(extractSlotMax, handler.getHandler().getSlots());
    }

    private int getInsertSlotMax(FilterableItemHandler handler){
        if(insertSlotMax == -1)
            return handler.getHandler().getSlots();
        return Math.min(insertSlotMax, handler.getHandler().getSlots());
    }
}
