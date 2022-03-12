package com.hollingsworth.arsnouveau.common.event;

import com.hollingsworth.arsnouveau.api.event.ITimedEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraftforge.common.util.FakePlayer;

public class OpenChestEvent implements ITimedEvent {
    public FakePlayer fakePlayer;
    public int duration;
    public BlockPos pos;

    public OpenChestEvent(FakePlayer fakePlayer, BlockPos pos, int duration){
        this.fakePlayer = fakePlayer;
        this.duration = duration;
        this.pos = pos;
    }

    public void open(){
        Level world = fakePlayer.level;
        if(world.getBlockEntity(pos) instanceof ChestBlockEntity){
            ((ChestBlockEntity) world.getBlockEntity(pos)).startOpen(fakePlayer);

        }
    }

    public void attemptClose(){
        Level world = fakePlayer.level;
        if(world.getBlockEntity(pos) instanceof ChestBlockEntity){
            ((ChestBlockEntity) world.getBlockEntity(pos)).stopOpen(fakePlayer);
        }
    }

    @Override
    public void tick(boolean serverSide) {
        duration--;
        if(duration <= 0){
            attemptClose();
        }
    }

    @Override
    public boolean isExpired() {
        return duration <= 0;
    }
}
