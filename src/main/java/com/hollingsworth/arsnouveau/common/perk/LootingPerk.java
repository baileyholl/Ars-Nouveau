package com.hollingsworth.arsnouveau.common.perk;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.perk.Perk;
import net.minecraft.resources.ResourceLocation;

public class LootingPerk extends Perk {

    public static final LootingPerk INSTANCE = new LootingPerk(ArsNouveau.prefix( "thread_drygmy"));

    public LootingPerk(ResourceLocation key) {
        super(key);
    }

    @Override
    public String getLangName() {
        return "The Drygmy";
    }

    @Override
    public String getLangDescription() {
        return "Grants an additional stack of looting.";
    }
}
