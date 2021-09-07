package com.hollingsworth.arsnouveau.common.entity.pathfinding;

import net.minecraft.util.math.BlockPos;

/**
 * Pathing constants class.
 */
public final class PathingConstants
{
    //  Debug Output
    public static final Object   debugNodeMonitor     = new Object();
    public static final BlockPos BLOCKPOS_IDENTITY    = new BlockPos(0, 0, 0);
    public static final BlockPos BLOCKPOS_UP          = new BlockPos(0, 1, 0);
    public static final BlockPos BLOCKPOS_DOWN        = new BlockPos(0, -1, 0);
    public static final BlockPos BLOCKPOS_NORTH       = new BlockPos(0, 0, -1);
    public static final BlockPos BLOCKPOS_SOUTH       = new BlockPos(0, 0, 1);
    public static final BlockPos BLOCKPOS_EAST        = new BlockPos(1, 0, 0);
    public static final BlockPos BLOCKPOS_WEST        = new BlockPos(-1, 0, 0);

    /**
     * Max jump height.
     */
    public static final double MAX_JUMP_HEIGHT = 1.3;

    /**
     * Half a block.
     */
    public static final double HALF_A_BLOCK = 0.5;

    /**
     * Private constructor to hide implicit one.
     */
    private PathingConstants()
    {
        /*
         * Intentionally left empty.
         */
    }
}