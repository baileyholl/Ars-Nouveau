package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class IntangibleAirTile extends ModdedTile implements ITickable {
    public int duration;
    public int maxLength;
    public int stateID;

    public IntangibleAirTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.INTANGIBLE_AIR_TYPE, pos, state);
    }

    @Override
    public void tick() {
        if (level.isClientSide)
            return;
        duration++;
        if (duration > maxLength) {
            level.setBlockAndUpdate(worldPosition, Block.stateById(stateID));

        }
        level.sendBlockUpdated(this.worldPosition, level.getBlockState(worldPosition), level.getBlockState(worldPosition), 2);
    }

    @Override
    public void load(CompoundTag nbt) {
        stateID = nbt.getInt("state_id");
        duration = nbt.getInt("duration");
        maxLength = nbt.getInt("max_length");
        super.load(nbt);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        tag.putInt("state_id", stateID);
        tag.putInt("duration", duration);
        tag.putInt("max_length", maxLength);
    }

}
