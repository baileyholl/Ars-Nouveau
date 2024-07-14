package com.hollingsworth.arsnouveau.common.util;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ItemUtil {

    public static boolean canStack(ItemStack a, ItemStack b) {
        ItemStack singleA = a.copyWithCount(1);
        ItemStack singleB = b.copyWithCount(1);
        return ItemEntity.areMergable(singleA, singleB);
    }

    public static boolean shrinkHandAndAddStack(ItemStack stack, InteractionHand hand, Player player){
        ItemStack heldStack = player.getItemInHand(hand);
        if(heldStack.isEmpty() || heldStack.getCount() == 1){
            player.setItemInHand(hand, stack);
            return true;
        }
        if(player.inventory.add(stack)){
            heldStack.shrink(1);
            return true;
        }
        return false;
    }

}
