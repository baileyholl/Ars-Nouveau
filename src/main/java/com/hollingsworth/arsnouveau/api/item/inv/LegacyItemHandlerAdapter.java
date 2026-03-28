package com.hollingsworth.arsnouveau.api.item.inv;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.RootCommitJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

/**
 * Adapts a legacy {@link IItemHandler} to the new {@link ResourceHandler}&lt;{@link ItemResource}&gt; API.
 *
 * <p>This allows deprecated {@link IItemHandler}-based inventories to be registered with the new
 * {@code Capabilities.Item.BLOCK} capability, which requires {@code ResourceHandler<ItemResource>}.
 *
 * <p>Transaction semantics are approximated: simulate operations are used for transaction previews,
 * and actual execution is deferred to transaction commit via {@link RootCommitJournal}.
 *
 * <p>This class is intentionally minimal — sufficient for capability registration and basic automation.
 */
@SuppressWarnings("deprecation")
public class LegacyItemHandlerAdapter implements ResourceHandler<ItemResource> {
    private final IItemHandler handler;

    public LegacyItemHandlerAdapter(IItemHandler handler) {
        this.handler = handler;
    }

    /** Wrap an {@link IItemHandler} as a {@link ResourceHandler}, returning null for null input. */
    public static ResourceHandler<ItemResource> of(IItemHandler handler) {
        return handler == null ? null : new LegacyItemHandlerAdapter(handler);
    }

    @Override
    public int size() {
        return handler.getSlots();
    }

    @Override
    public ItemResource getResource(int index) {
        ItemStack stack = handler.getStackInSlot(index);
        return stack.isEmpty() ? ItemResource.EMPTY : ItemResource.of(stack);
    }

    @Override
    public long getAmountAsLong(int index) {
        return handler.getStackInSlot(index).getCount();
    }

    @Override
    public long getCapacityAsLong(int index, ItemResource resource) {
        return handler.getSlotLimit(index);
    }

    @Override
    public boolean isValid(int index, ItemResource resource) {
        return handler.isItemValid(index, resource.toStack());
    }

    @Override
    public int insert(int index, ItemResource resource, int amount, TransactionContext transaction) {
        if (resource.isEmpty() || amount <= 0) return 0;
        ItemStack toInsert = resource.toStack(amount);
        ItemStack remaining = handler.insertItem(index, toInsert, true);
        int inserted = amount - remaining.getCount();
        if (inserted <= 0) return 0;

        // Register a commit callback to execute the actual (non-simulate) insert.
        final int toInsertFinal = inserted;
        new RootCommitJournal(() -> handler.insertItem(index, resource.toStack(toInsertFinal), false))
                .updateSnapshots(transaction);
        return toInsertFinal;
    }

    @Override
    public int extract(int index, ItemResource resource, int amount, TransactionContext transaction) {
        if (resource.isEmpty() || amount <= 0) return 0;
        ItemStack current = handler.getStackInSlot(index);
        if (current.isEmpty() || !ItemResource.of(current).equals(resource)) return 0;

        ItemStack extracted = handler.extractItem(index, amount, true);
        if (extracted.isEmpty()) return 0;
        int count = extracted.getCount();

        // Register a commit callback to execute the actual (non-simulate) extract.
        new RootCommitJournal(() -> handler.extractItem(index, count, false))
                .updateSnapshots(transaction);
        return count;
    }
}
