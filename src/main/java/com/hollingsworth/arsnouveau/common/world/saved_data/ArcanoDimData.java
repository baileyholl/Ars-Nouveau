package com.hollingsworth.arsnouveau.common.world.saved_data;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

public class ArcanoDimData extends SavedData {

    protected boolean bossSpawned = false;
    protected boolean bossDefeated = false;

    public void setBossSpawned(boolean bossSpawned) {
        this.bossSpawned = bossSpawned;
        setDirty();
    }

    public boolean isBossSpawned() {
        return bossSpawned;
    }

    public void setBossDefeated(boolean bossDefeated) {
        this.bossDefeated = bossDefeated;
        setDirty();
    }

    public boolean isBossDefeated() {
        return bossDefeated;
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag, HolderLookup.Provider provider) {
        compoundTag.putBoolean("bossSpawned", bossSpawned);
        compoundTag.putBoolean("bossDefeated", bossDefeated);
        return compoundTag;
    }

    public static ArcanoDimData load(CompoundTag compoundTag, HolderLookup.Provider provider) {
        ArcanoDimData data = new ArcanoDimData();
        data.bossDefeated = compoundTag.getBoolean("bossDefeated");
        data.bossSpawned = compoundTag.getBoolean("bossSpawned");
        return data;
    }


    public static ArcanoDimData from(ServerLevel level) {
        return level.getDataStorage()
                .computeIfAbsent(new SavedData.Factory<>(ArcanoDimData::new, ArcanoDimData::load, null), "arcano_jar_data");
    }
}
