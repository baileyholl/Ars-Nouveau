package com.hollingsworth.arsnouveau.common.perk;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.perk.Perk;
import net.minecraft.resources.ResourceLocation;

public class IgnitePerk extends Perk {
    public static IgnitePerk INSTANCE = new IgnitePerk(new ResourceLocation(ArsNouveau.MODID, "thread_ignite"));

    public IgnitePerk(ResourceLocation key) {
        super(key);
    }

    @Override
    public String getLangDescription() {
        return "Damaging effects cause the target to burn for a short duration before the effect resolves.";
    }

    @Override
    public String getLangName() {
        return "Ignition";
    }
}
