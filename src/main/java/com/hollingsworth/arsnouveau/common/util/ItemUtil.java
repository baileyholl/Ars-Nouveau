package com.hollingsworth.arsnouveau.common.util;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

public class ItemUtil {

    public static boolean canStack(ItemStack a, ItemStack b) {
        ItemStack singleA = a.copyWithCount(1);
        ItemStack singleB = b.copyWithCount(1);
        return ItemEntity.areMergable(singleA, singleB);
    }

}
