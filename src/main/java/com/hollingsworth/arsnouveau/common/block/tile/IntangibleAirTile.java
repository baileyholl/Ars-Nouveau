package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;

public class IntangibleAirTile extends BlockEntity implements TickableBlockEntity {
    public int duration;
    public int maxLength;
    public int stateID;

    public IntangibleAirTile() {
        super(BlockRegistry.INTANGIBLE_AIR_TYPE);
    }

    @Override
    public void tick() {
        if(level.isClientSide)
            return;
        duration++;
        if(duration > maxLength){
            level.setBlockAndUpdate(worldPosition, Block.stateById(stateID));

        }
        level.sendBlockUpdated(this.worldPosition, level.getBlockState(worldPosition),  level.getBlockState(worldPosition), 2);
    }

    @Override
    public void load(BlockState state, CompoundTag nbt) {
        stateID = nbt.getInt("state_id");
        duration = nbt.getInt("duration");
        maxLength = nbt.getInt("max_length");
        super.load(state, nbt);
    }

    @Override
    public CompoundTag save(CompoundTag compound) {
        compound.putInt("state_id", stateID);
        compound.putInt("duration", duration);
        compound.putInt("max_length", maxLength);
        return super.save(compound);
    }

    @Override
    @Nullable
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return new ClientboundBlockEntityDataPacket(this.worldPosition, 3, this.getUpdateTag());
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.save(new CompoundTag());
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        super.onDataPacket(net, pkt);
        handleUpdateTag(level.getBlockState(worldPosition),pkt.getTag());
    }
}
