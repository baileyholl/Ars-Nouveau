package com.hollingsworth.arsnouveau.common.event;

import com.hollingsworth.arsnouveau.api.event.ITimedEvent;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
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
        World world = fakePlayer.level;
        if(world.getBlockEntity(pos) instanceof ChestTileEntity){
            ((ChestTileEntity) world.getBlockEntity(pos)).startOpen(fakePlayer);

        }
    }

    public void attemptClose(){
        World world = fakePlayer.level;
        if(world.getBlockEntity(pos) instanceof ChestTileEntity){
            ((ChestTileEntity) world.getBlockEntity(pos)).stopOpen(fakePlayer);
        }
    }

    @Override
    public void tick() {
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
