package com.hollingsworth.nuggets.common.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.function.BiPredicate;

public class BlockPosHelpers {


    /**
     * Selects a solid position with air above
     */
    public static final BiPredicate<BlockGetter, BlockPos> TWO_HIGH_AIR_POS_SELECTOR = (world, pos) -> {
        return (world.getBlockState(pos).canOcclude() || world.getBlockState(pos).liquid()) && world.getBlockState(
                pos.above()).isAir() && world.getBlockState(pos.above(2)).isAir();
    };

    /**
     * Finds a spawnpoint randomly in a circular shape around the center Advances
     *
     * @param start      the center of the area to search for a spawn point
     * @param advancePos The position we advance towards
     * @return the calculated position
     */
    private static BlockPos findSpawnPointInDirections(
            Level level,
            final BlockPos start,
            final BlockPos advancePos) {
        BlockPos spawnPos = new BlockPos(start);
        Vec3 tempPos = new Vec3(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());

        final int xDiff = Math.abs(start.getX() - advancePos.getX());
        final int zDiff = Math.abs(start.getZ() - advancePos.getZ());

        Vec3 xzRatio = new Vec3(xDiff * (start.getX() < advancePos.getX() ? 1 : -1), 0, zDiff * (start.getZ() < advancePos.getZ() ? 1 : -1));
        // Reduce ratio to 3 chunks a step
        xzRatio = xzRatio.normalize().scale(3);

        int validChunkCount = 0;
        for (int i = 0; i < 10; i++) {
            if (level.isLoaded(BlockPos.containing(tempPos))) {
                tempPos = tempPos.add(16 * xzRatio.x, 0, 16 * xzRatio.z);

                if (level.isLoaded(BlockPos.containing(tempPos))) {
                    spawnPos = BlockPos.containing(tempPos);
                    validChunkCount++;
                    if (validChunkCount > 5) {
                        return spawnPos;
                    }
                } else {
                    break;
                }
            } else {
                break;
            }
        }

        if (!spawnPos.equals(start)) {
            return spawnPos;
        }

        return null;
    }

    public static BlockPos getRandomSpawn(BlockPos calcCenter, Level level) {

        // Get a random point on a circle around the colony,far out for the direction
        final int degree = level.random.nextInt(360);
        int x = (int) Math.round(20 * Math.cos(Math.toRadians(degree)));
        int z = (int) Math.round(20 * Math.sin(Math.toRadians(degree)));
        final BlockPos advanceTowards = calcCenter.offset(x, 0, z);

        BlockPos spawnPos = null;

        // 8 Tries
        for (int i = 0; i < 8; i++) {
            spawnPos = findSpawnPointInDirections(level, new BlockPos(calcCenter.getX(), calcCenter.getY(), calcCenter.getZ()), advanceTowards);
            if (spawnPos != null) {
                break;
            }
        }

        if (spawnPos == null) {
            return null;
        }


        return findAround(level, getFloor(spawnPos, level), 3, 30, TWO_HIGH_AIR_POS_SELECTOR);
    }


    /**
     * Calculates the floor level.
     *
     * @param position input position.
     * @param world    the world the position is in.
     * @return returns BlockPos position with air above.
     */
    @NotNull
    public static BlockPos getFloor(@NotNull final BlockPos position, @NotNull final Level world) {
        final BlockPos floor = getFloor(new BlockPos.MutableBlockPos(position.getX(), position.getY(), position.getZ()), 0, world);
        if (floor == null) {
            return position;
        }
        return floor;
    }


    /**
     * Calculates the floor level.
     *
     * @param position input position.
     * @param depth    the iteration depth.
     * @param world    the world the position is in.
     * @return returns BlockPos position with air above.
     */
    @Nullable
    public static BlockPos getFloor(BlockPos.MutableBlockPos position, int depth, @NotNull final Level world) {
        //If the position is floating in Air go downwards
        if (!solidOrLiquid(world, position)) {
            return getFloor(position.set(position.getX(), position.getY() - 1, position.getZ()), depth + 1, world);
        }
        //If there is no air above the block go upwards
        if (!solidOrLiquid(world, position.set(position.getX(), position.getY() + 1, position.getZ())) &&
                !solidOrLiquid(world, position.set(position.getX(), position.getY() + 2, position.getZ()))) {
            return position.immutable();
        }
        return getFloor(position.set(position.getX(), position.getY() + 1, position.getZ()), depth + 1, world);
    }

    /**
     * Checks if a blockPos in a world is solid or liquid.
     * <p>
     * Useful to find a suitable Place to stand. (avoid these blocks to find one)
     *
     * @param world    the world to look in
     * @param blockPos the blocks position
     * @return true if solid or liquid
     */
    public static boolean solidOrLiquid(@NotNull final Level world, @NotNull final BlockPos blockPos)
    {
        var state = world.getBlockState(blockPos);
        return state.isAir() || state.liquid();
    }


    /**
     * Returns the first air position near the given start. Advances vertically first then horizontally
     *
     * @param start           start position
     * @param horizontalRange horizontal search range
     * @param verticalRange   vertical search range
     * @param predicate       check predicate for the right block
     * @return position or null
     */
    public static BlockPos findAround(final Level world, final BlockPos start, final int verticalRange, final int horizontalRange, final BiPredicate<BlockGetter, BlockPos> predicate) {
        if (horizontalRange < 1 && verticalRange < 1) {
            return null;
        }

        if (predicate.test(world, start)) {
            return start;
        }

        BlockPos temp;
        int y = 0;
        int y_offset = 1;

        for (int i = 0; i < verticalRange + 2; i++) {
            for (int steps = 1; steps <= horizontalRange; steps++) {
                // Start topleft of middle point
                temp = start.offset(-steps, y, -steps);

                // X ->
                for (int x = 0; x <= steps; x++) {
                    temp = temp.offset(1, 0, 0);
                    if (predicate.test(world, temp)) {
                        return temp;
                    }
                }

                // X
                // |
                // v
                for (int z = 0; z <= steps; z++) {
                    temp = temp.offset(0, 0, 1);
                    if (predicate.test(world, temp)) {
                        return temp;
                    }
                }

                // < - X
                for (int x = 0; x <= steps; x++) {
                    temp = temp.offset(-1, 0, 0);
                    if (predicate.test(world, temp)) {
                        return temp;
                    }
                }

                // ^
                // |
                // X
                for (int z = 0; z <= steps; z++) {
                    temp = temp.offset(0, 0, -1);
                    if (predicate.test(world, temp)) {
                        return temp;
                    }
                }
            }

            y += y_offset;
            y_offset = y_offset > 0 ? y_offset + 1 : y_offset - 1;
            y_offset *= -1;

            if (!isInWorldHeight(start.getY() + y, world)) {
                return null;
            }
        }

        return null;
    }

    /**
     * Check if a given block y is within world bounds
     *
     * @param yBlock
     * @param world
     * @return
     */
    public static boolean isInWorldHeight(final int yBlock, final Level world)
    {
        final DimensionType dimensionType = world.dimensionType();
        return yBlock > getDimensionMinHeight(dimensionType) && yBlock < getDimensionMaxHeight(dimensionType);
    }


    /**
     * Returns a dimensions max height
     *
     * @param dimensionType
     * @return
     */
    public static int getDimensionMaxHeight(final DimensionType dimensionType)
    {
        return dimensionType.logicalHeight() + dimensionType.minY();
    }

    /**
     * Returns a dimension min height
     *
     * @param dimensionType
     * @return
     */
    public static int getDimensionMinHeight(final DimensionType dimensionType)
    {
        return dimensionType.minY();
    }

    public static double distanceBetween(BlockPos blockPos, BlockPos blockPos2) {
        return Math.sqrt(Math.pow(blockPos.getX() - blockPos2.getX(), 2) + Math.pow(blockPos.getY() - blockPos2.getY(), 2) + Math.pow(blockPos.getZ() - blockPos2.getZ(), 2));
    }
}
