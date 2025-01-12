package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
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
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(tag, pRegistries);
        stateID = tag.getInt("state_id");
        duration = tag.getInt("duration");
        maxLength = tag.getInt("max_length");
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(tag, pRegistries);
        tag.putInt("state_id", stateID);
        tag.putInt("duration", duration);
        tag.putInt("max_length", maxLength);
    }

}
