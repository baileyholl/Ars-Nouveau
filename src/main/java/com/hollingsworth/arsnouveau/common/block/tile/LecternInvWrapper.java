package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.item.inv.CombinedHandlerInv;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

public class LecternInvWrapper extends CombinedHandlerInv {
    public CraftingLecternTile lecternTile;
    private long simulatedAt;
    private ItemStack simulatedStack = ItemStack.EMPTY;
    private ItemStack simulatedRemainder = ItemStack.EMPTY;

    public LecternInvWrapper(CraftingLecternTile lecternTile, IItemHandler... itemHandler) {
        super(itemHandler);
        this.lecternTile = lecternTile;
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        if (!simulate) {
            simulatedRemainder = ItemStack.EMPTY;
            return lecternTile.pushStack(stack, null, false);
        }
        long gameTime = lecternTile.getLevel().getGameTime();
        if (gameTime == simulatedAt && !simulatedRemainder.isEmpty() && ItemStack.matches(stack, simulatedStack)) {
            return simulatedRemainder.copy();
        }
        ItemStack remainder = lecternTile.pushStack(stack, null, true);
        simulatedAt = gameTime;
        simulatedStack = stack.copy();
        // Disables the cache if we could insert any items, it is otherwise not safe to cache partial remainders
        simulatedRemainder = remainder.getCount() == stack.getCount() ? remainder.copy() : ItemStack.EMPTY;
        return remainder;
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (!simulate) {
            simulatedRemainder = ItemStack.EMPTY;
        }
        return super.extractItem(slot, amount, simulate);
    }
}
