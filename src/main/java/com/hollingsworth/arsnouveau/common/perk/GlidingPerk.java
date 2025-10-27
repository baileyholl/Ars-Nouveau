package com.hollingsworth.arsnouveau.common.perk;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.perk.Perk;
import com.hollingsworth.arsnouveau.api.perk.PerkSlot;
import net.minecraft.resources.ResourceLocation;

public class GlidingPerk extends Perk {

    public static final GlidingPerk INSTANCE = new GlidingPerk(ArsNouveau.prefix("thread_gliding"));

    public GlidingPerk(ResourceLocation key) {
        super(key);
    }

    @Override
    public PerkSlot minimumSlot() {
        return PerkSlot.THREE;
    }

    @Override
    public String getLangName() {
        return "Gliding";
    }

    @Override
    public String getLangDescription() {
        return "Allows you to glide as if you are wearing an elytra. Must be equipped in a slot of at least level 3.";
    }
}
