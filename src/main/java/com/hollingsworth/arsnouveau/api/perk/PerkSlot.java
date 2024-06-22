package com.hollingsworth.arsnouveau.api.perk;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.resources.ResourceLocation;

import java.util.concurrent.ConcurrentHashMap;

public class PerkSlot {
    public static ConcurrentHashMap<ResourceLocation, PerkSlot> PERK_SLOTS = new ConcurrentHashMap<>();

    public static final PerkSlot ONE = new PerkSlot(ArsNouveau.prefix( "one"), 1);
    public static final PerkSlot TWO = new PerkSlot(ArsNouveau.prefix( "two"), 2);
    public static final PerkSlot THREE = new PerkSlot(ArsNouveau.prefix( "three"), 3);

    public final ResourceLocation id;
    public final int value; // oneIndexed
    public PerkSlot(ResourceLocation id, int value){
        this.value = value;
        this.id = id;
    }

    static {
        PERK_SLOTS.put(ONE.id, ONE);
        PERK_SLOTS.put(TWO.id, TWO);
        PERK_SLOTS.put(THREE.id, THREE);
    }

}
