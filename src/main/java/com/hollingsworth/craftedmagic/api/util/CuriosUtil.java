package com.hollingsworth.craftedmagic.api.util;

import net.minecraft.entity.LivingEntity;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import top.theillusivec4.curios.api.CuriosAPI;

public class CuriosUtil {
    public static LazyOptional<IItemHandlerModifiable> getAllWornItems(LivingEntity living) {
        return CuriosAPI.getCuriosHandler(living).map(h -> {
            IItemHandlerModifiable[] invs = h.getCurioMap().values().toArray(new IItemHandlerModifiable[0]);
            return new CombinedInvWrapper(invs);
        });
    }
}
