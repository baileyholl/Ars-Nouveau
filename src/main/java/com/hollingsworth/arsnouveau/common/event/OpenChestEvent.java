package com.hollingsworth.arsnouveau.common.event;

import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.event.ITimedEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.neoforged.neoforge.common.util.FakePlayer;

public class OpenChestEvent implements ITimedEvent {
    ServerLevel level;
    public int duration;
    public BlockPos pos;
    FakePlayer fakePlayer;

    public OpenChestEvent(ServerLevel level, BlockPos pos, int duration) {
        this.duration = duration;
        this.level = level;
        this.pos = pos;
        fakePlayer = ANFakePlayer.getPlayer(level);
    }

    public void open() {
        try {
            if (level.getBlockEntity(pos) instanceof ChestBlockEntity chestBlockEntity) {
                fakePlayer.level = level;
                fakePlayer.containerCounter = fakePlayer.containerCounter % 100 + 1;
                fakePlayer.setPos(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
                fakePlayer.containerMenu = chestBlockEntity.createMenu(fakePlayer.containerCounter, fakePlayer.inventory, fakePlayer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void attemptClose() {
        try {
            if (level.getBlockEntity(pos) instanceof ChestBlockEntity) {
                fakePlayer.level = level;
                fakePlayer.setPos(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
                fakePlayer.containerMenu = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void tick(boolean serverSide) {
        duration--;
        if (duration <= 0) {
            attemptClose();
        }
    }

    @Override
    public boolean isExpired() {
        return duration <= 0;
    }
}
