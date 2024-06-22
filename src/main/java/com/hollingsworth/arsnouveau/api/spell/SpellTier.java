package com.hollingsworth.arsnouveau.api.spell;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.resources.ResourceLocation;

import java.util.concurrent.ConcurrentHashMap;

public class SpellTier {
    public static final ConcurrentHashMap<Integer, SpellTier> SPELL_TIER_MAP = new ConcurrentHashMap<>();

    public static SpellTier ONE = createTier(ArsNouveau.prefix( "one"), 1);
    public static SpellTier TWO = createTier(ArsNouveau.prefix( "two"), 2);
    public static SpellTier THREE = createTier(ArsNouveau.prefix( "three"), 3);
    public static SpellTier CREATIVE = createTier(ArsNouveau.prefix( "creative"), 99);

    public int value;
    public ResourceLocation id;

    @Deprecated //TODO:1.20 Make private in favor of create tier method
    public SpellTier(ResourceLocation id, int value) {
        this.value = value;
        this.id = id;
        if(value > 99){
            throw new IllegalArgumentException("Spell tier cannot be greater than 99");
        }
    }

    public static SpellTier createTier(ResourceLocation id, int value){
        SpellTier tier = new SpellTier(id, value);
        SPELL_TIER_MAP.put(value, tier);
        return tier;
    }
}