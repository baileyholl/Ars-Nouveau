package com.hollingsworth.arsnouveau.common.perk;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.perk.Perk;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class EmptyPerk extends Perk {
    public static Identifier registryName = ArsNouveau.prefix("blank_thread");

    public static EmptyPerk INSTANCE = new EmptyPerk(registryName);

    public EmptyPerk(Identifier key) {
        super(key);
    }

    public String getName() {
        return Component.translatable("item.ars_nouveau.blank_thread").getString();
    }

    @Override
    public String getLangName() {
        return "Blank";
    }

    @Override
    public String getDescriptionKey() {
        return "tooltip.ars_nouveau.blank_thread";
    }
}
