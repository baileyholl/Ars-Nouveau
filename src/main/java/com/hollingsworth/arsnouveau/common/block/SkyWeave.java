package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.SkyBlockTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SkyWeave extends MirrorWeave implements ITickableBlock{

    public SkyWeave(Properties properties) {
        super(properties);
    }

    public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
        return new SkyBlockTile(p_153215_, p_153216_);
    }
}
