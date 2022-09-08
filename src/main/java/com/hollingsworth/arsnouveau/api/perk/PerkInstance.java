package com.hollingsworth.arsnouveau.api.perk;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.common.perk.StarbunclePerk;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class PerkInstance {

    private PerkSlot slot;
    private IPerk perk;

    public PerkInstance(CompoundTag tag){
        ResourceLocation perkId = new ResourceLocation(tag.getString("perkId"));
        perk = ArsNouveauAPI.getInstance().getPerkMap().getOrDefault(perkId, StarbunclePerk.INSTANCE);
        slot = PerkSlot.PERK_SLOTS.getOrDefault(new ResourceLocation(tag.getString("slotId")), PerkSlot.ONE);
    }

    public PerkInstance(PerkSlot slot, IPerk perk){
        this.slot = slot;
        this.perk = perk;
    }

    public PerkSlot getSlot(){
        return slot;
    }

    public IPerk getPerk(){
        return perk;
    }
}
