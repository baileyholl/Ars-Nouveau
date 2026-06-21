package com.hollingsworth.arsnouveau.common.world.saved_data;

import com.hollingsworth.arsnouveau.common.entity.arcano_boss.ArcanoBoss;
import com.hollingsworth.arsnouveau.common.util.ANCodecs;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.UUID;

public class ArcanoDimData extends SavedData {

    public static final BlockPos BOSS_POS = new BlockPos(15, 2, 15);
    public static final BlockPos REWARD_POS = new BlockPos(15, 2, 15);

    protected boolean bossSpawned = false;
    protected boolean bossDefeated = false;
    protected int stageLevel = 0;
    protected BlockPos rewardBlockPos = null;
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

    public int getStageLevel() {
        return stageLevel;
    }

    public void onEntityEntered(ServerLevel serverLevel) {
        if (bossDefeated) {
            spawnRewardBlock(serverLevel);
            return;
        }
        if (!bossSpawned) {
            spawnBoss(serverLevel);
        }
    }

    public void reset(ServerLevel serverLevel) {
        if (bossUUID != null) {
            Entity existing = serverLevel.getEntity(bossUUID);
            if (existing != null) {
                existing.remove(Entity.RemovalReason.DISCARDED);
            }
        }
        removeRewardBlock(serverLevel);
        bossSpawned = false;
        bossDefeated = false;
        stageLevel = 0;
        bossUUID = null;
        onEntityEntered(serverLevel);
    }

    public void onBossDefeated(ServerLevel serverLevel) {
        bossDefeated = true;
        bossUUID = null;
        spawnRewardBlock(serverLevel);
        setDirty();
    }

    public void continueToNextStage(ServerLevel serverLevel) {
        if (!bossDefeated) {
            return;
        }
        removeRewardBlock(serverLevel);
        stageLevel++;
        bossSpawned = false;
        bossDefeated = false;
        bossUUID = null;
        spawnBoss(serverLevel);
        setDirty();
    }

    public boolean isRewardBlockAt(ServerLevel serverLevel, BlockPos pos) {
        return rewardBlockPos != null && rewardBlockPos.equals(pos) && serverLevel.getBlockState(pos).is(BlockRegistry.ARCANO_REWARD.get());
    }

    private void spawnBoss(ServerLevel serverLevel) {
        setBossSpawned(true);
        ArcanoBoss arcanoBoss = new ArcanoBoss(serverLevel);
        arcanoBoss.setPos(BOSS_POS.getX() + 0.5, BOSS_POS.getY(), BOSS_POS.getZ() + 0.5);
        arcanoBoss.isSetupPhase = true;
        bossUUID = arcanoBoss.getUUID();
        serverLevel.addFreshEntity(arcanoBoss);
        setDirty();
    }

    private void spawnRewardBlock(ServerLevel serverLevel) {
        BlockPos pos = rewardBlockPos == null ? REWARD_POS : rewardBlockPos;
        serverLevel.setBlockAndUpdate(pos, BlockRegistry.ARCANO_REWARD.get().defaultBlockState());
        rewardBlockPos = pos;
        setDirty();
    }

    private void removeRewardBlock(ServerLevel serverLevel) {
        if (rewardBlockPos != null) {
            removeRewardBlockAt(serverLevel, rewardBlockPos);
        }
        removeRewardBlockAt(serverLevel, REWARD_POS);
        rewardBlockPos = null;
        setDirty();
    }

    private void removeRewardBlockAt(ServerLevel serverLevel, BlockPos pos) {
        BlockState state = serverLevel.getBlockState(pos);
        if (state.is(BlockRegistry.ARCANO_REWARD.get())) {
            serverLevel.removeBlock(pos, false);
        }
    }

    @Override
    public boolean isDirty() {
        return true;
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag, HolderLookup.Provider provider) {
        compoundTag.putBoolean("bossSpawned", bossSpawned);
        compoundTag.putBoolean("bossDefeated", bossDefeated);
        compoundTag.putInt("stageLevel", stageLevel);
        if (rewardBlockPos != null) {
            compoundTag.put("rewardBlockPos", ANCodecs.encode(BlockPos.CODEC, rewardBlockPos));
        }
        if (bossUUID != null) {
            compoundTag.putUUID("bossUUID", bossUUID);
        }
        return compoundTag;
    }

    public static ArcanoDimData load(CompoundTag compoundTag, HolderLookup.Provider provider) {
        ArcanoDimData data = new ArcanoDimData();
        data.bossDefeated = compoundTag.getBoolean("bossDefeated");
        data.bossSpawned = compoundTag.getBoolean("bossSpawned");
        data.stageLevel = compoundTag.getInt("stageLevel");
        if (compoundTag.contains("rewardBlockPos")) {
            data.rewardBlockPos = ANCodecs.decode(BlockPos.CODEC, compoundTag.get("rewardBlockPos"));
        }
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
