package com.hollingsworth.arsnouveau.common.capability;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.familiar.AbstractFamiliarHolder;
import com.hollingsworth.arsnouveau.api.familiar.IFamiliar;
import com.hollingsworth.arsnouveau.api.registry.FamiliarRegistry;
import com.hollingsworth.arsnouveau.common.lib.LibEntityNames;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;


public class FamiliarData {
    public static final String ENTITY_TAG = "entityTag";
    public static final String FAMILIAR_ID = "familiarID";
    public AbstractFamiliarHolder familiarHolder;
    public CompoundTag entityTag;

    public FamiliarData(ResourceLocation entityID) {
        this.familiarHolder = FamiliarRegistry.getFamiliarHolderMap().get(entityID);
        this.entityTag = new CompoundTag();
    }

    public FamiliarData(CompoundTag tag) {
        this.entityTag = tag.contains(ENTITY_TAG) ? tag.getCompound(ENTITY_TAG) : new CompoundTag();
        this.familiarHolder = FamiliarRegistry.getFamiliarHolderMap().getOrDefault(new ResourceLocation(tag.getString(FAMILIAR_ID)),
                FamiliarRegistry.getFamiliarHolderMap().get(ArsNouveau.prefix( LibEntityNames.FAMILIAR_WIXIE)));
    }

    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        tag.putString(FAMILIAR_ID, familiarHolder.getRegistryName().toString());
        tag.put(ENTITY_TAG, entityTag);
        return tag;
    }

    public IFamiliar getEntity(Level level) {
        IFamiliar familiar = familiarHolder.getSummonEntity(level, entityTag);
        familiar.setHolderID(familiarHolder.getRegistryName());
        return familiar;
    }
}
