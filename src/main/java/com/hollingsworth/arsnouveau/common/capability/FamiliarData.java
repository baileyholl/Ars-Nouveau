package com.hollingsworth.arsnouveau.common.capability;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.familiar.AbstractFamiliarHolder;
import com.hollingsworth.arsnouveau.api.familiar.IFamiliar;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;


public class FamiliarData {
    public static final String ENTITY_TAG = "entityTag";
    public AbstractFamiliarHolder familiarHolder;
    public CompoundTag entityTag;

    public FamiliarData(String entityID){
        this.familiarHolder = ArsNouveauAPI.getInstance().getFamiliarHolderMap().get(entityID);
        this.entityTag = new CompoundTag();
    }

    public FamiliarData(CompoundTag tag){
        this.entityTag = tag.contains(ENTITY_TAG) ? tag.getCompound(ENTITY_TAG) : new CompoundTag();
        this.familiarHolder = ArsNouveauAPI.getInstance().getFamiliarHolderMap().getOrDefault(tag.getString("familiar"),ArsNouveauAPI.getInstance().getFamiliarHolderMap().get("wixie"));
    }

    public CompoundTag toTag(){
        CompoundTag tag = new CompoundTag();
        tag.putString("familiar", familiarHolder.id);
        tag.put(ENTITY_TAG, entityTag);
        return tag;
    }

    public IFamiliar getEntity(Level level){
        IFamiliar familiar = familiarHolder.getSummonEntity(level, entityTag);
        familiar.setHolderID(familiarHolder.id);
        return familiar;
    }
}
