package com.hollingsworth.arsnouveau.api.familiar;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;

public class PersistentFamiliarData<T extends Entity> {
    public Component name;

    public PersistentFamiliarData(CompoundTag tag){
        this.name = tag.contains("name") ? Component.Serializer.fromJson(tag.getString("name")) : null;
    }

    public void setData(T entity){
        if(name != null)
            entity.setCustomName(name);
    }

    public CompoundTag toTag(CompoundTag tag){
        if(name != null)
            tag.putString("name", Component.Serializer.toJson(name));
        return tag;
    }
}
