package com.hollingsworth.arsnouveau.api.util;

import com.hollingsworth.arsnouveau.common.block.PortalBlock;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.Optional;
import java.util.function.Predicate;

public abstract class PortalFrameTester {
    protected Predicate<BlockState> VALID_FRAME = null;
    protected int foundPortalBlocks;
    public BlockPos lowerCorner;
    protected Level world;

    public abstract PortalFrameTester init(Level world, BlockPos blockPos, Direction.Axis axis,  Predicate<BlockState> foundations);

    public abstract Optional<PortalFrameTester> getNewPortal(Level worldAccess, BlockPos blockPos, Direction.Axis axis,  Predicate<BlockState> foundations);

    public abstract Optional<PortalFrameTester> getOrEmpty(Level worldAccess, BlockPos blockPos, Predicate<PortalFrameTester> predicate, Direction.Axis axis,  Predicate<BlockState> foundations);

    public abstract boolean isAlreadyLitPortalFrame();

    public abstract boolean isValidFrame();

    public abstract void lightPortal(Block frameBlock);

    public abstract void createPortal(Level world, BlockPos pos, BlockState frameBlock, Direction.Axis axis);

    public abstract boolean isRequestedSize(int attemptWidth, int attemptHeight);

//    public abstract BlockLocating.Rectangle getRectangle();

    public abstract Direction.Axis getAxis1();

    public abstract Direction.Axis getAxis2();

    public abstract BlockPos doesPortalFitAt(Level world, BlockPos attemptPos, Direction.Axis axis);

//    public abstract Vec3d getEntityOffsetInPortal(BlockLocating.Rectangle arg, Entity entity, Direction.Axis portalAxis);
//
//    public abstract TeleportTarget getTPTargetInPortal(BlockLocating.Rectangle portalRect, Direction.Axis portalAxis, Vec3d prevOffset, Entity entity);

    protected BlockPos getLowerCorner(BlockPos blockPos, Direction.Axis axis1, Direction.Axis axis2) {
        if (!validStateInsidePortal(world.getBlockState(blockPos), VALID_FRAME))
            return null;
        int offsetX = 1;
        while (validStateInsidePortal(world.getBlockState(blockPos.relative(axis1, -offsetX)), VALID_FRAME)) {
            offsetX++;
            if (offsetX > 20) return null;
        }
        blockPos = blockPos.relative(axis1, -(offsetX - 1));
        int offsetY = 1;
        while (blockPos.getY() - offsetY > 0 && validStateInsidePortal(world.getBlockState(blockPos.relative(axis2, -offsetY)), VALID_FRAME)) {
            offsetY++;
            if (offsetY > 20) return null;
        }
        return blockPos.relative(axis2, -(offsetY - 1));
    }

    protected int getSize(Direction.Axis axis, int minSize, int maxSize) {
        for (int i = 1; i <= maxSize; i++) {
            BlockState blockState = this.world.getBlockState(this.lowerCorner.relative(axis, i));
            if (!validStateInsidePortal(blockState, VALID_FRAME)) {
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
                if (world.getBlockState(this.lowerCorner.relative(axis1, i).relative(axis2, j)).getBlock() == BlockRegistry.PORTAL_BLOCK)
                    foundPortalBlocks++;
    }

    public static boolean validStateInsidePortal(BlockState blockState,  Predicate<BlockState>  foundations) {
        return blockState.isAir() || blockState.getMaterial().isReplaceable() || blockState.getBlock() instanceof PortalBlock;
    }
}