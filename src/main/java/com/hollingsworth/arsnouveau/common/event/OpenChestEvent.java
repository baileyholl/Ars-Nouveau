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
        try {
            if (world.getBlockEntity(pos) instanceof ChestBlockEntity chestBlockEntity) {
                fakePlayer.nextContainerCounter();
                fakePlayer.containerMenu = chestBlockEntity.createMenu(fakePlayer.containerCounter, fakePlayer.inventory, fakePlayer);
                fakePlayer.setPos(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void attemptClose(){
        Level world = fakePlayer.level;
        try {
            if (world.getBlockEntity(pos) instanceof ChestBlockEntity) {
                fakePlayer.setPos(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
                fakePlayer.containerMenu = null;
            }
        }catch (Exception e){
            e.printStackTrace();
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
