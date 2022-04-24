package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.ScryerCrystalTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class ScryerCrystal extends TickableModBlock{
    public ScryerCrystal(Properties properties, String registry) {
        super(properties, registry);
    }

    public ScryerCrystal(String registryName) {
        super(registryName);
    }

    public ScryerCrystal(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new ScryerCrystalTile(pPos, pState);
    }
}
