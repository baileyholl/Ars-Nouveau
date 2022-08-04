package com.hollingsworth.arsnouveau.api.perk;

import net.minecraft.network.chat.Component;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

/**
 * Represents an object or thing that stores a set of perks.
 */
public interface IPerkHolder<T> {

    @Nonnull PerkSet getPerkSet();

    int getMaxSlots();

    default boolean isEmpty(){
        return getPerkSet().isEmpty();
    }

    default void appendPerkTooltip(List<Component> tooltip, T obj){
        if(getPerkSet().getPerkMap().isEmpty())
            return;
        for(Map.Entry<IPerk, Integer> entry : getPerkSet().getPerkMap().entrySet()){
            tooltip.add(Component.literal(entry.getKey().getRegistryName() + ":"  + entry.getValue()));
        }
//        tooltip.add(Component.translatable("tooltip.ars_nouveau.armor.perks", getPerkSet().getPerkMap().size()).withStyle(ChatFormatting.LIGHT_PURPLE));
    }
}
