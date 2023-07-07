package com.hollingsworth.arsnouveau.api.util;


import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;
import java.util.function.Predicate;

public class FlatPortalAreaHelper extends PortalFrameTester {
    public int xSize = -1, zSize = -1;
    public final int maxXSize = 21, maxZSize = 21;

    public FlatPortalAreaHelper() {
    }

    public FlatPortalAreaHelper init(Level world, BlockPos blockPos, Direction.Axis axis, Predicate<BlockState> foundations) {
        VALID_FRAME = foundations;
        this.world = world;
        this.lowerCorner = this.getLowerCorner(blockPos, Direction.Axis.X, Direction.Axis.Z);
        this.foundPortalBlocks = 0;
        if (lowerCorner == null) {
            lowerCorner = blockPos;
            xSize = zSize = 0;
        } else {
            this.xSize = this.getSize(Direction.Axis.X, 1, maxXSize);
            if (this.xSize > 0) {
                this.zSize = this.getSize(Direction.Axis.Z, 1, maxZSize);
                if (checkForValidFrame(Direction.Axis.X, Direction.Axis.Z, xSize, zSize)) {
                    countExistingPortalBlocks(Direction.Axis.X, Direction.Axis.Z, xSize, zSize);
                } else {
                    lowerCorner = null;
                    xSize = zSize = 1;
                }
            }
        }
        return this;
    }

    public Optional<PortalFrameTester> getOrEmpty(Level worldAccess, BlockPos blockPos, Predicate<PortalFrameTester> predicate, Direction.Axis axis, Predicate<BlockState> foundations) {
        return Optional.of((PortalFrameTester) new FlatPortalAreaHelper().init(worldAccess, blockPos, axis, foundations)).filter(predicate);
    }

    public boolean isValidFrame() {
        return this.lowerCorner != null && xSize >= 1 && zSize >= 1 && xSize < maxXSize && zSize < maxZSize;
    }

    @Override
    public void lightPortal(Block frameBlock) {
        BlockPos.betweenClosed(this.lowerCorner, this.lowerCorner.relative(Direction.Axis.X, this.xSize - 1).relative(Direction.Axis.Z, this.zSize - 1)).forEach((blockPos) -> {
            this.world.setBlock(blockPos, Blocks.SNOW_BLOCK.defaultBlockState(), 18);
        });
    }

    private void fillAirAroundPortal(Level world, BlockPos pos) {
        if (world.getBlockState(pos).isSolid())
            world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
    }

    private void placeLandingPad(Level world, BlockPos pos, BlockState frameBlock) {
        if (!world.getBlockState(pos).isSolid())
            world.setBlockAndUpdate(pos, frameBlock);
    }
}