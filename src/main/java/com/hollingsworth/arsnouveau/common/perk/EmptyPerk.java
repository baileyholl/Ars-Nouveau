package com.hollingsworth.arsnouveau.common.perk;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.perk.Perk;
import net.minecraft.resources.ResourceLocation;

public class EmptyPerk extends Perk {
    public static ResourceLocation registryName = new ResourceLocation(ArsNouveau.MODID, "blank_thread");

    public static EmptyPerk INSTANCE = new EmptyPerk(registryName);

    public EmptyPerk(ResourceLocation key) {
        super(key);
    }

    @Override
    public String getDescriptionKey() {
        return "tooltip.ars_nouveau.blank_thread";
    }
}
