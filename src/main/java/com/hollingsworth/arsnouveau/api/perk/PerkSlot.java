package com.hollingsworth.arsnouveau.api.perk;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.documentation.DocAssets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public record PerkSlot(ResourceLocation id, int value, DocAssets.BlitInfo icon) {

    public PerkSlot(ResourceLocation id, int value){
        this(id, value, DocAssets.ICON_THREAD_TIER3);
    }
    public static final Codec<PerkSlot> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(PerkSlot::id),
            Codec.INT.fieldOf("value").forGetter(PerkSlot::value)
    ).apply(instance, PerkSlot::new));

    public static final Codec<List<PerkSlot>> LIST_CODEC = Codec.list(CODEC);

    public static final Codec<List<List<PerkSlot>>> TIERED_LIST_CODEC = Codec.list(LIST_CODEC);

    public static ConcurrentHashMap<ResourceLocation, PerkSlot> PERK_SLOTS = new ConcurrentHashMap<>();

    public static final PerkSlot ONE = new PerkSlot(ArsNouveau.prefix( "one"), 1, DocAssets.ICON_THREAD_TIER1);
    public static final PerkSlot TWO = new PerkSlot(ArsNouveau.prefix( "two"), 2, DocAssets.ICON_THREAD_TIER2);
    public static final PerkSlot THREE = new PerkSlot(ArsNouveau.prefix( "three"), 3, DocAssets.ICON_THREAD_TIER3);

    static {
        PERK_SLOTS.put(ONE.id, ONE);
        PERK_SLOTS.put(TWO.id, TWO);
        PERK_SLOTS.put(THREE.id, THREE);
    }
}
