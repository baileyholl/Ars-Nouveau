package com.hollingsworth.arsnouveau.api.perk;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.util.PerkUtil;
import com.hollingsworth.arsnouveau.common.perk.StarbunclePerk;
import com.hollingsworth.arsnouveau.common.util.SerializationUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

/**
 * A utility wrapper for mapping a collection of perks from armor, tools, or capabilities.
 *
 */
public class PerkSet {

    private final Map<IPerk, PerkSlot> perks;
    // Get a callback when a perk is added or removed.
    public Runnable onMutated;

    public PerkSet() {
        this.perks = new HashMap<>();
    }

    public PerkSet(CompoundTag tag){
        this();
        if(tag == null)
            return;
        ListTag listTag = tag.getList("perks", SerializationUtil.COMPOUND_TAG_TYPE);
        for(int i = 0; i < listTag.size(); i++){
            CompoundTag perkTag = listTag.getCompound(i);
            ResourceLocation perkId = new ResourceLocation(perkTag.getString("perkId"));
            setPerk(ArsNouveauAPI.getInstance().getPerkMap().getOrDefault(perkId, StarbunclePerk.INSTANCE), PerkSlot.PERK_SLOTS.getOrDefault(new ResourceLocation(perkTag.getString("slotId")), PerkSlot.ONE));
        }
    }

    public CompoundTag serialize(){
        CompoundTag tag = new CompoundTag();
        ListTag listTag = new ListTag();
        perks.forEach((perk, level) -> {
            CompoundTag perkTag = new CompoundTag();
            perkTag.putString("perkId", perk.getRegistryName().toString());
            perkTag.putString("slotId", level.id.toString());
            listTag.add(perkTag);
        });
        tag.put("perks", listTag);
        return tag;
    }

    public Map<IPerk, PerkSlot> getPerkMap() {
        return perks;
    }

    public void clearPerks(){
        perks.clear();
        setChanged();
    }

    public void setPerk(IPerk perk,  PerkSlot perkSlot){
        perks.put(perk, perkSlot);
        setChanged();
    }

    public void removePerk(IPerk perk){
        perks.remove(perk);
        setChanged();
    }

    public boolean hasPerk(IPerk perk){
        return perks.containsKey(perk);
    }

    public boolean isEmpty(){
        return perks.isEmpty();
    }

    public void setChanged(){
        if(onMutated != null)
            onMutated.run();
    }
}
