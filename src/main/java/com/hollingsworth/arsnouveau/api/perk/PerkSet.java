package com.hollingsworth.arsnouveau.api.perk;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
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

    private final Map<IPerk, Integer> perks;
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
            addPerk(ArsNouveauAPI.getInstance().getPerkMap().getOrDefault(perkId, StarbunclePerk.INSTANCE), perkTag.getInt("level"));
        }
    }

    public CompoundTag serialize(){
        CompoundTag tag = new CompoundTag();
        ListTag listTag = new ListTag();
        perks.forEach((perk, level) -> {
            CompoundTag perkTag = new CompoundTag();
            perkTag.putString("perkId", perk.getRegistryName().toString());
            perkTag.putInt("level", level);
            listTag.add(perkTag);
        });
        tag.put("perks", listTag);
        return tag;
    }

    /**
     * Returns the applicable count of a perk, ignoring wasted perks
     */
    public int countForPerk(IPerk perk){
        return Math.min(perks.getOrDefault(perk, 0), perk.getCountCap());
    }

    public Map<IPerk, Integer> getPerkMap() {
        return perks;
    }

    public Integer addPerk(IPerk perk, int count){
        if(perks.containsKey(perk)){
            perks.put(perk, perks.get(perk) + count);
        } else {
            perks.put(perk, count);
        }
        setChanged();
        return perks.get(perk);
    }

    public Integer setPerk(IPerk perk, int count){
        perks.put(perk, count);
        setChanged();
        return perks.get(perk);
    }

    public Integer removePerk(IPerk perk){
        Integer count = perks.remove(perk);
        setChanged();
        return count;
    }

    public boolean isPerkCapped(IPerk perk){
        return perks.getOrDefault(perk, 0) >= perk.getCountCap();
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
