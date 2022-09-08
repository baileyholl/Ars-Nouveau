package com.hollingsworth.arsnouveau.common.perk;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.perk.Perk;
import com.hollingsworth.arsnouveau.api.perk.PerkSlot;
import net.minecraft.resources.ResourceLocation;

public class TotemPerk extends Perk {
    public static TotemPerk INSTANCE = new TotemPerk(new ResourceLocation(ArsNouveau.MODID, "thread_totem"));

    public TotemPerk(ResourceLocation key) {
        super(key);
    }

    @Override
    public String getLangDescription() {
        return "Once every time you sleep, you will nullify death as if holding a Totem of the Undying. Requires a tier 3 slot.";
    }

    @Override
    public PerkSlot minimumSlot() {
        return PerkSlot.THREE;
    }

    @Override
    public String getLangName() {
        return "Undying";
    }
}
