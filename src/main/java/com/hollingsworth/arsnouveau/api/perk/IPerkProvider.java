package com.hollingsworth.arsnouveau.api.perk;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Represents an object or thing that stores a set of perks.
 */
public interface IPerkProvider {

    @Nonnull PerkSet getPerkSet();

    default void appendPerkTooltip(List<Component> tooltip){
        if(getPerkSet().getPerkMap().isEmpty())
            return;
        tooltip.add(Component.translatable("tooltip.ars_nouveau.armor.perks", getPerkSet().getPerkMap().size()).withStyle(ChatFormatting.LIGHT_PURPLE));
    }
}
