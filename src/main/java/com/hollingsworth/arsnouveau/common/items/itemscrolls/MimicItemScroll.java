package com.hollingsworth.arsnouveau.common.items.itemscrolls;

import com.hollingsworth.arsnouveau.common.items.ItemScroll;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

public class MimicItemScroll extends ItemScroll {

    public MimicItemScroll() {
        super();
    }

    public MimicItemScroll(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult use(@NotNull Level pLevel, @NotNull Player pPlayer, @NotNull InteractionHand pUsedHand) {
        return InteractionResult.PASS;
    }

    @Override
    public SortPref getSortPref(ItemStack stackToStore, ItemStack scrollStack, IItemHandler inventory) {
        for (int i = 0; i < inventory.getSlots(); i++) {
            ItemStack inventoryStack = inventory.getStackInSlot(i);
            if (inventoryStack.isEmpty())
                continue;
            if (ItemStack.isSameItem(inventory.getStackInSlot(i), stackToStore)) {
                return SortPref.HIGHEST;
            }
        }
        return SortPref.INVALID;
    }
}
