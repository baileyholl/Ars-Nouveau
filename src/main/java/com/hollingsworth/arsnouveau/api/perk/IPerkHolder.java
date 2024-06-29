package com.hollingsworth.arsnouveau.api.perk;

import com.hollingsworth.arsnouveau.api.util.RomanNumber;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an object or thing that stores a set of perks.
 */
public interface IPerkHolder<T> {

    default List<PerkInstance> getPerkInstances(){
        List<PerkInstance> perkInstances = new ArrayList<>();
        List<PerkSlot> slots = new ArrayList<>(getSlotsForTier());
        List<IPerk> perks = getPerks();
        for(int i = 0; i < slots.size(); i++){
            if(i < perks.size()) {
                perkInstances.add(new PerkInstance(slots.get(i), perks.get(i)));
            }
        }
        return perkInstances;
    }

    List<IPerk> getPerks();

    T setPerks(List<IPerk> perks);

    List<PerkSlot> getSlotsForTier();

    default boolean isEmpty(){
        return getPerks().isEmpty();
    }

    default void appendPerkTooltip(List<Component> tooltip, T obj){

        for(PerkInstance perkInstance : getPerkInstances()){
            IPerk perk = perkInstance.getPerk();
            ResourceLocation location = perk.getRegistryName();
            tooltip.add(Component.literal(Component.translatable("item." + location.getNamespace() + "." + location.getPath()).getString()
                    + " " + RomanNumber.toRoman(perkInstance.getSlot().value())));
        }
        int missing = getSlotsForTier().size() - getPerkInstances().size();
        for(int i = 0; i < missing; i++){
            PerkSlot slot = new ArrayList<>(getSlotsForTier()).subList(getPerkInstances().size(), getSlotsForTier().size()).get(i);
            tooltip.add(Component.literal(Component.translatable("Empty").getString() + " " + RomanNumber.toRoman(slot.value())).withStyle(ChatFormatting.RED).withStyle(ChatFormatting.ITALIC));
        }
    }

    int getTier();

    T setTier(int tier);

    @Nullable CompoundTag getTagForPerk(IPerk perk);

    T setTagForPerk(IPerk perk, CompoundTag tag);
}
