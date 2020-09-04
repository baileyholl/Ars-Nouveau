package com.hollingsworth.arsnouveau.api.util;

import net.minecraft.entity.LivingEntity;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import top.theillusivec4.curios.api.CuriosApi;

import javax.annotation.Nonnull;

public class CuriosUtil {
    public static LazyOptional<IItemHandlerModifiable> getAllWornItems(@Nonnull LivingEntity living) {
        return  CuriosApi.getCuriosHelper().getEquippedCurios(living);
    }
}
