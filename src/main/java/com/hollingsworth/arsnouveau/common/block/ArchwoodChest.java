package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.ArchwoodChestTile;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class ArchwoodChest extends ChestBlock {
    public ArchwoodChest() {
        super(() -> BlockRegistry.ARCHWOOD_CHEST_TILE.get(), SoundEvents.CHEST_OPEN, SoundEvents.CHEST_CLOSE, BlockRegistry.newBlockProperties().strength(2.5F).sound(SoundType.WOOD));
    }

    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new ArchwoodChestTile(pos, state);
    }

}
