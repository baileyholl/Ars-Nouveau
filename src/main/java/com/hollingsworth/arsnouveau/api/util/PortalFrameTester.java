package com.hollingsworth.arsnouveau.api.util;

import com.hollingsworth.arsnouveau.common.block.PortalBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;
import java.util.function.Predicate;

public abstract class PortalFrameTester {
    protected Predicate<BlockState> VALID_FRAME = null;
    protected int foundPortalBlocks;
    public BlockPos lowerCorner;
    protected Level world;

    public abstract PortalFrameTester init(Level world, BlockPos blockPos, Direction.Axis axis, Predicate<BlockState> foundations);

    public abstract Optional<PortalFrameTester> getOrEmpty(Level worldAccess, BlockPos blockPos, Predicate<PortalFrameTester> predicate, Direction.Axis axis, Predicate<BlockState> foundations);

    public abstract boolean isValidFrame();

    public abstract void lightPortal(Block frameBlock);

    protected BlockPos getLowerCorner(BlockPos blockPos, Direction.Axis axis1, Direction.Axis axis2) {
        if (!validStateInsidePortal(world.getBlockState(blockPos)))
            return null;
        int offsetX = 1;
        while (validStateInsidePortal(world.getBlockState(blockPos.relative(axis1, -offsetX)))) {
            offsetX++;
            if (offsetX > 20) return null;
        }
        blockPos = blockPos.relative(axis1, -(offsetX - 1));
        int offsetY = 1;
        while (validStateInsidePortal(world.getBlockState(blockPos.relative(axis2, -offsetY)))) {
            offsetY++;
            if (offsetY > 20) return null;
        }
        return blockPos.relative(axis2, -(offsetY - 1));
    }

    protected int getSize(Direction.Axis axis, int minSize, int maxSize) {
        for (int i = 1; i <= maxSize; i++) {
            BlockState blockState = this.world.getBlockState(this.lowerCorner.relative(axis, i));
            if (!validStateInsidePortal(blockState)) {
                if (VALID_FRAME.test(blockState)) {
                    return i >= minSize ? i : 0;

                }
                break;
            }
        }
        return 0;
    }

    protected boolean checkForValidFrame(Direction.Axis axis1, Direction.Axis axis2, int size1, int size2) {
        BlockPos checkPos = lowerCorner.mutable();
        for (int i = 0; i < size1; i++) {
            if (!VALID_FRAME.test(world.getBlockState(checkPos.relative(axis2, -1))) || !VALID_FRAME.test(world.getBlockState(checkPos.relative(axis2, size2))))
                return false;
            checkPos = checkPos.relative(axis1, 1);
        }
        checkPos = lowerCorner.mutable();
        for (int i = 0; i < size2; i++) {
            if (!VALID_FRAME.test(world.getBlockState(checkPos.relative(axis1, -1))) || !VALID_FRAME.test(world.getBlockState(checkPos.relative(axis1, size1))))
                return false;
            checkPos = checkPos.relative(axis2, 1);
        }
        return true;
    }

    protected void countExistingPortalBlocks(Direction.Axis axis1, Direction.Axis axis2, int size1, int size2) {
        for (int i = 0; i < size1; i++)
            for (int j = 0; j < size2; j++)
                if (world.getBlockState(this.lowerCorner.relative(axis1, i).relative(axis2, j)).getBlock() instanceof PortalBlock)
                    foundPortalBlocks++;
    }

    public static boolean validStateInsidePortal(BlockState blockState) {
        return blockState.isAir() || blockState.canBeReplaced() || blockState.getBlock() instanceof PortalBlock;
    }
}