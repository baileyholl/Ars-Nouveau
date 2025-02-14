package com.hollingsworth.arsnouveau.api.event;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.Event;

public class SuccessfulTreeGrowthEvent extends Event {
    public ServerLevel level;
    public BlockPos pos;
    public BlockState sapling;

    public SuccessfulTreeGrowthEvent(ServerLevel level, BlockPos pos, BlockState sapling) {
        this.level = level;
        this.pos = pos;
        this.sapling = sapling;
    }
}
