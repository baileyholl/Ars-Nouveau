package com.hollingsworth.arsnouveau.common.perk;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.perk.Perk;
import net.minecraft.resources.ResourceLocation;

public class VampiricPerk extends Perk {
    public static VampiricPerk INSTANCE = new VampiricPerk(new ResourceLocation(ArsNouveau.MODID, "thread_life_drain"));
    public VampiricPerk(ResourceLocation key) {
        super(key);
    }

    @Override
    public String getLangDescription() {
        return "Dealing damage with spells heals you for 20%% per level of the damage dealt.";
    }

    @Override
    public String getLangName() {
        return "Life Drain";
    }
}
