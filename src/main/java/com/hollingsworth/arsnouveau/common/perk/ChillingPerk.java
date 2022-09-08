package com.hollingsworth.arsnouveau.common.perk;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.perk.Perk;
import net.minecraft.resources.ResourceLocation;

public class ChillingPerk extends Perk {
    public static ChillingPerk INSTANCE = new ChillingPerk(new ResourceLocation(ArsNouveau.MODID, "thread_chilling"));

    public ChillingPerk(ResourceLocation key) {
        super(key);
    }

    @Override
    public String getLangDescription() {
        return "Damaging effects inflict a short amount of Freezing before the effect resolves.";
    }

    @Override
    public String getLangName() {
        return "Chilling";
    }

}
