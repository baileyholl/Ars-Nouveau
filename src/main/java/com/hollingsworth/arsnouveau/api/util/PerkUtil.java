package com.hollingsworth.arsnouveau.api.util;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.perk.IPerkHolder;
import com.hollingsworth.arsnouveau.api.perk.IPerkProvider;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public class PerkUtil {

    public static @Nullable IPerkHolder<ItemStack> getPerkHolder(ItemStack stack){
        IPerkProvider<ItemStack> holder = ArsNouveauAPI.getInstance().getPerkProvider(stack.getItem());
        return holder == null ? null : holder.getPerkHolder(stack);
    }

}
