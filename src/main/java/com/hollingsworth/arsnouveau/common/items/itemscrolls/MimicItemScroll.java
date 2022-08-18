package com.hollingsworth.arsnouveau.common.items.itemscrolls;

import com.hollingsworth.arsnouveau.common.items.ItemScroll;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;

public class MimicItemScroll extends ItemScroll {

    public MimicItemScroll() {
        super();
    }

    public MimicItemScroll(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        return InteractionResultHolder.pass(pPlayer.getItemInHand(pUsedHand));
    }

    @Override
    public SortPref getSortPref(ItemStack stackToStore, ItemStack scrollStack, IItemHandler inventory) {
        for (int i = 0; i < inventory.getSlots(); i++) {
            ItemStack inventoryStack = inventory.getStackInSlot(i);
            if(inventoryStack.isEmpty())
                continue;
            if (inventory.getStackInSlot(i).sameItemStackIgnoreDurability(stackToStore)) {
                return SortPref.HIGHEST;
            }
        }
        return SortPref.INVALID;
    }
}
