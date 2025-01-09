package com.hollingsworth.arsnouveau.api.spell;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.documentation.DocAssets;
import net.minecraft.resources.ResourceLocation;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class SpellTier {
    public static final ConcurrentHashMap<Integer, SpellTier> SPELL_TIER_MAP = new ConcurrentHashMap<>();

    public static SpellTier ONE = createTier(ArsNouveau.prefix( "one"), 1, () -> DocAssets.TIER_ONE);
    public static SpellTier TWO = createTier(ArsNouveau.prefix( "two"), 2, () -> DocAssets.TIER_TWO);
    public static SpellTier THREE = createTier(ArsNouveau.prefix( "three"), 3, () -> DocAssets.TIER_THREE);
    public static SpellTier CREATIVE = createTier(ArsNouveau.prefix( "creative"), 99, () -> DocAssets.TIER_THREE);

    public int value;
    public ResourceLocation id;
    public Supplier<DocAssets.BlitInfo> docInfo;

    private SpellTier(ResourceLocation id, int value, Supplier<DocAssets.BlitInfo> docInfo) {
        this.value = value;
        this.id = id;
        this.docInfo = docInfo;
        if(value > 99){
            throw new IllegalArgumentException("Spell tier cannot be greater than 99");
        }
    }

    @Deprecated(forRemoval = true)
    public static SpellTier createTier(ResourceLocation id, int value){
        return SpellTier.createTier(id, value, () -> DocAssets.TIER_THREE);
    }

    public static SpellTier createTier(ResourceLocation id, int value, Supplier<DocAssets.BlitInfo> docInfo){
        SpellTier tier = new SpellTier(id, value, docInfo);
        SPELL_TIER_MAP.put(value, tier);
        return tier;
    }
}