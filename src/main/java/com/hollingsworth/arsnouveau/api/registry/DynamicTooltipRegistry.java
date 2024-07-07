package com.hollingsworth.arsnouveau.api.registry;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class DynamicTooltipRegistry {
    private static Set<DataComponentType> dataTypes = ConcurrentHashMap.newKeySet();

    public static void register(DataComponentType type){
        dataTypes.add(type);
    }

    public static void appendTooltips(ItemStack stack, Item.TooltipContext context, Consumer<Component> adder, TooltipFlag flag){
        for(DataComponentType type : dataTypes){
            stack.addToTooltip(type, context, adder, flag);
        }
    }
}
