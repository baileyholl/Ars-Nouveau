package com.hollingsworth.arsnouveau.api.perk;

import com.hollingsworth.arsnouveau.api.util.RomanNumber;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Represents an object or thing that stores a set of perks.
 */
public interface IPerkHolder<T> {

    default List<PerkInstance> getPerkInstances(){
        List<PerkInstance> perkInstances = new ArrayList<>();
        List<PerkSlot> slots = new ArrayList<>(getSlotsForTier());
        List<IPerk> perks = getPerks();
        slots.sort(Comparator.comparingInt(a -> a.value));
        for (PerkSlot slot : slots) {
            for (IPerk perk : perks) {
                perkInstances.add(new PerkInstance(slot, perk));
            }
        }
        return perkInstances;
    }

    List<IPerk> getPerks();

    void setPerks(List<IPerk> perks);

    List<PerkSlot> getSlotsForTier();

    default boolean isEmpty(){
        return getPerks().isEmpty();
    }

    default void appendPerkTooltip(List<Component> tooltip, T obj){
        if(isEmpty())
            return;
        for(PerkInstance perkInstance : getPerkInstances()){
            IPerk perk = perkInstance.getPerk();
            ResourceLocation location = perk.getRegistryName();
            tooltip.add(Component.literal(Component.translatable("item." + location.getNamespace() + "." + location.getPath()).getString()
                    + " " + RomanNumber.toRoman(perkInstance.getSlot().value)));
        }
    }
}
