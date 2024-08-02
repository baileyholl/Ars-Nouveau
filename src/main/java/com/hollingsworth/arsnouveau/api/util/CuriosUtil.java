package com.hollingsworth.arsnouveau.api.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import javax.annotation.Nullable;
import java.util.Optional;

public class CuriosUtil {

    public static @Nullable IItemHandlerModifiable getAllWornItems(@NotNull LivingEntity living) {
        Optional<ICuriosItemHandler> curioInv = CuriosApi.getCuriosInventory(living);
        return curioInv.map(ICuriosItemHandler::getEquippedCurios).orElse(null);
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
