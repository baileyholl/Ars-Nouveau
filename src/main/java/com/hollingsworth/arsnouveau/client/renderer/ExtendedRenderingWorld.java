package com.hollingsworth.arsnouveau.client.renderer;

import com.hollingsworth.nuggets.client.rendering.FakeRenderingWorld;
import com.hollingsworth.nuggets.client.rendering.StatePos;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;

public class ExtendedRenderingWorld extends FakeRenderingWorld {

    public ExtendedRenderingWorld(Level world) {
        super(world);
    }

    public ExtendedRenderingWorld(Level rWorld, ArrayList<StatePos> coordinates, BlockPos lookingAt) {
        super(rWorld, coordinates, lookingAt);
    }


    @Override
    public BlockState getBlockState(BlockPos pos) {
        return getBlockStateWithoutReal(pos);
    }

    @Override
    public int getBrightness(LightLayer lightType, BlockPos blockPos) {
        return 15;
    }
}
