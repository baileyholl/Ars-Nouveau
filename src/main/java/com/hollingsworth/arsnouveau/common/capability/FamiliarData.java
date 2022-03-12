package com.hollingsworth.arsnouveau.common.capability;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.familiar.AbstractFamiliarHolder;
import com.hollingsworth.arsnouveau.api.familiar.IFamiliar;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;


public class FamiliarData {

    public AbstractFamiliarHolder familiarHolder;
    CompoundTag entityTag;

    public FamiliarData(String entityID){
        this.familiarHolder = ArsNouveauAPI.getInstance().getFamiliarHolderMap().get(entityID);
        this.entityTag = new CompoundTag();
    }

    public FamiliarData(CompoundTag tag){
        this.entityTag = tag.contains("entityTag") ? tag.getCompound("entityTag") : new CompoundTag();
        this.familiarHolder = ArsNouveauAPI.getInstance().getFamiliarHolderMap().getOrDefault(tag.getString("familiar"),ArsNouveauAPI.getInstance().getFamiliarHolderMap().get("wixie"));
    }

    public CompoundTag toTag(){
        CompoundTag tag = new CompoundTag();
        tag.putString("familiar", familiarHolder.id);
        tag.put("entityTag", entityTag);
        return tag;
    }

    public IFamiliar getEntity(Level level){
        return familiarHolder.getSummonEntity(level, entityTag);
    }
}
