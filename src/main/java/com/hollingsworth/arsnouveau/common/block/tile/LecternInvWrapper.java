package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.item.inv.CombinedHandlerInv;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

public class LecternInvWrapper extends CombinedHandlerInv {
    public StorageLecternTile lecternTile;

    public LecternInvWrapper(StorageLecternTile lecternTile, IItemHandler... itemHandler) {
        super(itemHandler);
        this.lecternTile = lecternTile;
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int slot) {
        return super.getStackInSlot(slot);
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        return !simulate ? lecternTile.pushStack(stack, null) : super.insertItem(slot, stack, simulate);
    }
}
