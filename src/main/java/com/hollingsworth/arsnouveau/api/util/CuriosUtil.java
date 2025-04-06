package com.hollingsworth.arsnouveau.api.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.event.CurioChangeEvent;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.common.event.CuriosEventHandler;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CuriosUtil {
    public static Map<LivingEntity, IItemHandlerModifiable> WORN_CACHE = new HashMap<>();

    public static @Nullable IItemHandlerModifiable getAllWornItems(@NotNull LivingEntity living) {
        return WORN_CACHE.computeIfAbsent(living, e -> {
            Optional<ICuriosItemHandler> curioInv = CuriosApi.getCuriosInventory(living);
            //noinspection OptionalIsPresent
            if (curioInv.isEmpty()) {
                return null;
            }
            return curioInv.get().getEquippedCurios();
        });
    }

    public static boolean hasItem(@Nullable LivingEntity entity, ItemStack stack) {
        if (entity == null)
            return false;
        IItemHandlerModifiable items = CuriosUtil.getAllWornItems(entity);
        if (items != null) {
            for (int i = 0; i < items.getSlots(); i++) {
                if (ItemStack.isSameItem(stack, items.getStackInSlot(i))) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean hasItem(LivingEntity entity, Item item) {
        return hasItem(entity, new ItemStack(item));
    }
}
