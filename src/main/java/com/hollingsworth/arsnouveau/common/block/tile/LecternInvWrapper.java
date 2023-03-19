package com.hollingsworth.arsnouveau.common.block.tile;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.NotNull;

public class LecternInvWrapper extends CombinedInvWrapper {
    public StorageLecternTile lecternTile;

    public LecternInvWrapper(StorageLecternTile lecternTile, IItemHandlerModifiable... itemHandler)
    {
        super(itemHandler);
        this.lecternTile = lecternTile;
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int slot) {
        return super.getStackInSlot(slot);
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        return !simulate ? lecternTile.pushStack(stack) : super.insertItem(slot, stack, simulate);
    }
}
