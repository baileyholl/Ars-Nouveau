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
    public boolean canceled;

    public OpenChestEvent(FakePlayer fakePlayer, BlockPos pos, int duration){
        this.fakePlayer = fakePlayer;
        this.duration = duration;
        this.pos = pos;
    }

    public void open(){
        World world = fakePlayer.world;
        if(world.getTileEntity(pos) instanceof ChestTileEntity){
            ((ChestTileEntity) world.getTileEntity(pos)).openInventory(fakePlayer);

        }
    }

    public void attemptClose(){
        World world = fakePlayer.world;
        if(world.getTileEntity(pos) instanceof ChestTileEntity){
            ((ChestTileEntity) world.getTileEntity(pos)).closeInventory(fakePlayer);
        }
    }

    @Override
    public void tick() {
        if(canceled){
            return;
        }

        duration--;
        if(duration <= 0){
            attemptClose();
        }
    }

    @Override
    public boolean isExpired() {
        return duration <= 0 || canceled;
    }
}
