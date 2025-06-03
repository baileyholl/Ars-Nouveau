package com.hollingsworth.arsnouveau.api.config;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public interface IItemConfigurable extends IConfigurable {
    @Override
    @Nullable
    default String getSubFolder() {
        return "items";
    }

    static <T> void updateComponent(ModConfigSpec spec, DataComponentType<T> component, ItemStack stack, T defaultValue, UnaryOperator<T> updater) {
        stack.update(component, defaultValue, (comp) -> {
            if (spec != null && spec.isLoaded()) {
                return updater.apply(comp);
            }
            return comp;
        });
    }

    static <T> void updateComponent(ModConfigSpec spec, Supplier<DataComponentType<T>> component, ItemStack stack, T defaultValue, UnaryOperator<T> updater) {
        updateComponent(spec, component.get(), stack, defaultValue, updater);
    }
}
