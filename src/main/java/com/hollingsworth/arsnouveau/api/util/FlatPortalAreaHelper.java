package com.hollingsworth.arsnouveau.api.util;


import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.Optional;
import java.util.function.Predicate;

public class FlatPortalAreaHelper extends PortalFrameTester {
    public int xSize = -1, zSize = -1;
    public final int maxXSize = 21, maxZSize = 21;

    public FlatPortalAreaHelper() {
    }

    public FlatPortalAreaHelper init(Level world, BlockPos blockPos, Direction.Axis axis,  Predicate<BlockState>  foundations) {
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

    public Optional<PortalFrameTester> getNewPortal(Level worldAccess, BlockPos blockPos, Direction.Axis axis, Predicate<BlockState>  foundations) {
        return getOrEmpty(worldAccess, blockPos, (areaHelper) -> {
            return areaHelper.isValidFrame() && areaHelper.foundPortalBlocks == 0;
        }, axis, foundations);
    }

    public Optional<PortalFrameTester> getOrEmpty(Level worldAccess, BlockPos blockPos, Predicate<PortalFrameTester> predicate, Direction.Axis axis, Predicate<BlockState>  foundations) {
        return Optional.of((PortalFrameTester) new FlatPortalAreaHelper().init(worldAccess, blockPos, axis, foundations)).filter(predicate);
    }


    public boolean isAlreadyLitPortalFrame() {
        return this.isValidFrame() && this.foundPortalBlocks == this.xSize * this.zSize;
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

//    public void lightPortal(Block frameBlock) {
//        PortalLink link = CustomPortalApiRegistry.getPortalLinkFromBase(frameBlock);
//        BlockState blockState = CustomPortalHelper.blockWithAxis(link != null ? link.getPortalBlock().getDefaultState() : CustomPortalsMod.getDefaultPortalBlock().getDefaultState(), Direction.Axis.Y);
//        BlockPos.iterate(this.lowerCorner, this.lowerCorner.offset(Direction.Axis.X, this.xSize - 1).offset(Direction.Axis.Z, this.zSize - 1)).forEach((blockPos) -> {
//            this.world.setBlockState(blockPos, blockState, 18);
//        });
//    }

    @Override
    public void createPortal(Level world, BlockPos pos, BlockState frameBlock, Direction.Axis axis) {
        for (int i = -1; i < 3; i++) {
            world.setBlockAndUpdate(pos.relative(Direction.Axis.X, i).relative(Direction.Axis.Z, -1), frameBlock);
            world.setBlockAndUpdate(pos.relative(Direction.Axis.X, i).relative(Direction.Axis.Z, 2), frameBlock);

            world.setBlockAndUpdate(pos.relative(Direction.Axis.Z, i).relative(Direction.Axis.X, -1), frameBlock);
            world.setBlockAndUpdate(pos.relative(Direction.Axis.Z, i).relative(Direction.Axis.X, 2), frameBlock);
        }
        for (int i = 0; i < 2; i++) {
            placeLandingPad(world, pos.relative(Direction.Axis.X, i).below(), frameBlock);
            placeLandingPad(world, pos.relative(Direction.Axis.X, i).relative(Direction.Axis.Z, 1).below(), frameBlock);

            fillAirAroundPortal(world, pos.relative(Direction.Axis.X, i).above());
            fillAirAroundPortal(world, pos.relative(Direction.Axis.X, i).relative(Direction.Axis.Z, 1).above());
            fillAirAroundPortal(world, pos.relative(Direction.Axis.X, i).above(2));
            fillAirAroundPortal(world, pos.relative(Direction.Axis.X, i).relative(Direction.Axis.Z, 1).above(2));
        }
        //inits this instance based off of the newly created portal;
        this.lowerCorner = pos;
        this.xSize = zSize = 2;
        this.world = world;
        this.foundPortalBlocks = 4;
        lightPortal(frameBlock.getBlock());
    }

    private void fillAirAroundPortal(Level world, BlockPos pos) {
        if (world.getBlockState(pos).getMaterial().isSolid())
            world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
    }

    private void placeLandingPad(Level world, BlockPos pos, BlockState frameBlock) {
        if (!world.getBlockState(pos).getMaterial().isSolid())
            world.setBlockAndUpdate(pos, frameBlock);
    }

    @Override
    public boolean isRequestedSize(int attemptWidth, int attemptHeight) {
        return ((xSize == attemptWidth || attemptHeight == 0) && (zSize == attemptHeight) || attemptWidth == 0) ||
                ((xSize == attemptHeight || attemptHeight == 0) && (zSize == attemptWidth || attemptWidth == 0));
    }

//    @Override
//    public BlockLocating.Rectangle getRectangle() {
//        return new BlockLocating.Rectangle(lowerCorner, xSize, zSize);
//    }

    @Override
    public Direction.Axis getAxis1() {
        return Direction.Axis.X;
    }

    @Override
    public Direction.Axis getAxis2() {
        return Direction.Axis.Z;
    }

    @Override
    public BlockPos doesPortalFitAt(Level world, BlockPos attemptPos, Direction.Axis axis) {
        return attemptPos;
//        BlockLocating.Rectangle rect = BlockLocating.getLargestRectangle(attemptPos.up(), Direction.Axis.X, 4, Direction.Axis.Z, 4, blockPos -> {
//            return world.getBlockState(blockPos).getMaterial().isSolid() &&
//                    !world.getBlockState(blockPos.up()).getMaterial().isSolid() && !world.getBlockState(blockPos.up()).getMaterial().isLiquid() &&
//                    !world.getBlockState(blockPos.up(2)).getMaterial().isSolid() && !world.getBlockState(blockPos.up(2)).getMaterial().isLiquid();
//        });
//        return rect.width >= 4 && rect.height >= 4 ? rect.lowerLeft : null;
    }

//    @Override
//    public Vec3d getEntityOffsetInPortal(BlockLocating.Rectangle arg, Entity entity, Direction.Axis portalAxis) {
//        EntityDimensions entityDimensions = entity.getDimensions(entity.getPose());
//        double xSize = arg.width - entityDimensions.width;
//        double zSize = arg.height - entityDimensions.width;
//
//        double deltaX = MathHelper.getLerpProgress(entity.getX(), arg.lowerLeft.getX(), arg.lowerLeft.getX() + xSize);
//        double deltaY = MathHelper.getLerpProgress(entity.getY(), arg.lowerLeft.getY() - 1, arg.lowerLeft.getY() + 1);
//        double deltaZ = MathHelper.getLerpProgress(entity.getZ(), arg.lowerLeft.getZ(), arg.lowerLeft.getZ() + zSize);
//
//        return new Vec3d(deltaX, deltaY, deltaZ);
//    }
//
//    @Override
//    public TeleportTarget getTPTargetInPortal(BlockLocating.Rectangle portalRect, Direction.Axis portalAxis, Vec3d prevOffset, Entity entity) {
//        EntityDimensions entityDimensions = entity.getDimensions(entity.getPose());
//        double xSize = portalRect.width - entityDimensions.width;
//        double zSize = portalRect.height - entityDimensions.width;
//
//        double x = MathHelper.lerp(prevOffset.x, portalRect.lowerLeft.getX(), portalRect.lowerLeft.getX() + xSize);
//        double y = MathHelper.lerp(prevOffset.y, portalRect.lowerLeft.getY() - 1, portalRect.lowerLeft.getY() + 1);
//        double z = MathHelper.lerp(prevOffset.z, portalRect.lowerLeft.getZ(), portalRect.lowerLeft.getZ() + zSize);
//
//        y = Math.max(y, portalRect.lowerLeft.getY());
//        return new TeleportTarget(new Vec3d(x, y, z), entity.getVelocity(), entity.getYaw(), entity.getPitch());
//    }
}