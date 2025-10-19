package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.world.saved_data.JarDimData;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public class DimBoundary extends ModBlock {

    public DimBoundary(Properties properties) {
        super(properties);
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        System.out.println("destroyed");
        if (level instanceof ServerLevel serverLevel) {
            JarDimData jarDimData = JarDimData.from(serverLevel);
            System.out.println(jarDimData.getEnteredFrom(player.getUUID()));
        }
        return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
    }
}
