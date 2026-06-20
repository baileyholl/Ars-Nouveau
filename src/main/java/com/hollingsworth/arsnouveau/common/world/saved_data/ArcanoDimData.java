package com.hollingsworth.arsnouveau.common.world.saved_data;

import com.hollingsworth.arsnouveau.common.entity.arcano_boss.ArcanoBoss;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.UUID;

public class ArcanoDimData extends SavedData {

    protected boolean bossSpawned = false;
    protected boolean bossDefeated = false;
    public UUID bossUUID = null;

    public void setBossSpawned(boolean bossSpawned) {
        this.bossSpawned = bossSpawned;
    }

    public boolean isBossSpawned() {
        return bossSpawned;
    }

    public void setBossDefeated(boolean bossDefeated) {
        this.bossDefeated = bossDefeated;
    }

    public boolean isBossDefeated() {
        return bossDefeated;
    }

    public void onEntityEntered(ServerLevel serverLevel) {
        if (!bossSpawned) {
            setBossSpawned(true);
            ArcanoBoss arcanoBoss = new ArcanoBoss(serverLevel);
            arcanoBoss.setPos(15.5, 2, 15.5);
            arcanoBoss.isSetupPhase = true;
            bossUUID = arcanoBoss.getUUID();
            serverLevel.addFreshEntity(arcanoBoss);
        }
    }

    public void reset(ServerLevel serverLevel) {
        if (bossUUID != null) {
            Entity existing = serverLevel.getEntity(bossUUID);
            if (existing != null) {
                existing.remove(Entity.RemovalReason.DISCARDED);
            }
        }
        bossSpawned = false;
        bossDefeated = false;
        bossUUID = null;
        onEntityEntered(serverLevel);
    }

    @Override
    public boolean isDirty() {
        return true;
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag, HolderLookup.Provider provider) {
        compoundTag.putBoolean("bossSpawned", bossSpawned);
        compoundTag.putBoolean("bossDefeated", bossDefeated);
        if (bossUUID != null) {
            compoundTag.putUUID("bossUUID", bossUUID);
        }
        return compoundTag;
    }

    public static ArcanoDimData load(CompoundTag compoundTag, HolderLookup.Provider provider) {
        ArcanoDimData data = new ArcanoDimData();
        data.bossDefeated = compoundTag.getBoolean("bossDefeated");
        data.bossSpawned = compoundTag.getBoolean("bossSpawned");
        if (compoundTag.hasUUID("bossUUID")) {
            data.bossUUID = compoundTag.getUUID("bossUUID");
        }
        return data;
    }


    public static ArcanoDimData from(ServerLevel level) {
        return level.getDataStorage()
                .computeIfAbsent(new SavedData.Factory<>(ArcanoDimData::new, ArcanoDimData::load, null), "arcano_jar_data");
    }
}
