package com.hollingsworth.arsnouveau.api.spell;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.resources.ResourceLocation;

import java.util.concurrent.ConcurrentHashMap;

public class SpellTier {
    public static final ConcurrentHashMap<Integer, SpellTier> SPELL_TIER_MAP = new ConcurrentHashMap<>();

    public static SpellTier ONE = createTier(new ResourceLocation(ArsNouveau.MODID, "one"), 1);
    public static SpellTier TWO = createTier(new ResourceLocation(ArsNouveau.MODID, "two"), 2);
    public static SpellTier THREE = createTier(new ResourceLocation(ArsNouveau.MODID, "three"), 3);
    public static SpellTier CREATIVE = createTier(new ResourceLocation(ArsNouveau.MODID, "creative"), 99);

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
//    static {
//        SPELL_TIER_MAP.put(ONE.value, ONE);
//        SPELL_TIER_MAP.put(TWO.value, TWO);
//        SPELL_TIER_MAP.put(THREE.value, THREE);
//        SPELL_TIER_MAP.put(CREATIVE.value, CREATIVE);
//    }
}