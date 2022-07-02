package com.hollingsworth.arsnouveau.api.spell;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.resources.ResourceLocation;

public class SpellTier {
    public static SpellTier ONE = new SpellTier(new ResourceLocation(ArsNouveau.MODID, "one"), 1);
    public static SpellTier TWO = new SpellTier(new ResourceLocation(ArsNouveau.MODID, "two"), 2);
    public static SpellTier THREE = new SpellTier(new ResourceLocation(ArsNouveau.MODID, "three"), 3);

    public int value;
    public ResourceLocation id;

    public SpellTier(ResourceLocation id, int value) {
        this.value = value;
        this.id = id;
    }
}