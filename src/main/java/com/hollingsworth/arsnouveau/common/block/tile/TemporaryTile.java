package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class TemporaryTile extends MirrorWeaveTile implements ITickable {

    public int tickDuration;
    public Long gameTime = null;

    public TemporaryTile(BlockPos pos, BlockState state) {
        this(BlockRegistry.TEMPORARY_TILE.get(), pos, state);
    }

    public TemporaryTile(BlockEntityType type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.mimicState = getDefaultBlockState();
    }

    @Override
    public void tick() {
        if (level == null || level.isClientSide) return;

        if (gameTime == null) gameTime = level.getGameTime();

        if (level.getGameTime() < gameTime + tickDuration) return;

        level.setBlock(worldPosition, Blocks.AIR.defaultBlockState(), 2);
        level.updateNeighborsAt(worldPosition, level.getBlockState(worldPosition).getBlock());
        for (Direction d : Direction.values()) {
            level.updateNeighborsAt(worldPosition.relative(d), this.getBlockState().getBlock());
        }
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(tag, pRegistries);
        if (gameTime != null) tag.putLong("gameTime", gameTime);
        tag.putInt("tickDuration", tickDuration);
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        tickDuration = pTag.getInt("tickDuration");
        if (pTag.contains("gameTime")) {
            gameTime = pTag.getLong("gameTime");
        }
    }

    public BlockState getDefaultBlockState() {
        return Blocks.AIR.defaultBlockState();
    }
}
