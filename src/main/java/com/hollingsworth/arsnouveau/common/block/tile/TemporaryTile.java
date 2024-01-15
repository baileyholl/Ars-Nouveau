package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class TemporaryTile extends MirrorWeaveTile implements ITickable {

    public int tickDuration;

    public TemporaryTile(BlockPos pos, BlockState state) {
        this(BlockRegistry.TEMPORARY_TILE.get(), pos, state);
    }

    public TemporaryTile(BlockEntityType type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.mimicState = getDefaultBlockState();
    }

    @Override
    public void tick() {
        if(!level.isClientSide){
            tickDuration--;
            if(tickDuration <= 0){
                level.setBlock(worldPosition, Blocks.AIR.defaultBlockState(), 2);
                level.updateNeighborsAt(worldPosition, level.getBlockState(worldPosition).getBlock());
                for (Direction d : Direction.values()) {
                    level.updateNeighborsAt(worldPosition.relative(d), this.getBlockState().getBlock());
                }
            }
        }
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("tickDuration", tickDuration);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        tickDuration = pTag.getInt("tickDuration");
    }

    public BlockState getDefaultBlockState(){
        return Blocks.AIR.defaultBlockState();
    }
}
