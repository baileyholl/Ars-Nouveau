package com.hollingsworth.arsnouveau.common.entity.pathfinding;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.Level;


public abstract class AbstractAdvancedPathNavigate extends GroundPathNavigation {

    /**
     * Type of restriction.
     */
    public enum RestrictionType {
        NONE,
        XZ,
        XYZ
    }

    //  Parent class private members
    protected final Mob ourEntity;
    protected BlockPos destination;
    protected double walkSpeedFactor = 1.0D;
    protected BlockPos originalDestination;

    /**
     * The navigators node costs
     */
    private com.hollingsworth.arsnouveau.common.entity.pathfinding.PathingOptions pathingOptions = new com.hollingsworth.arsnouveau.common.entity.pathfinding.PathingOptions();

    public AbstractAdvancedPathNavigate(final Mob entityLiving, final Level worldIn) {
        super(entityLiving, worldIn);
        this.ourEntity = mob;
    }

    /**
     * Get the destination from the path.
     *
     * @return the destination position.
     */
    public BlockPos getDestination() {
        return destination;
    }


    /**
     * Used to path away from a position.
     *
     * @param currentPosition the position to avoid.
     * @param range           the range he should move out of.
     * @param speed           the speed to run at.
     * @param safeDestination if the destination is save and should be set.
     * @return the result of the pathing.
     */
    public abstract PathResult moveAwayFromXYZ(final BlockPos currentPosition, final double range, final double speed, final boolean safeDestination);

    /**
     * Try to move to a certain position.
     *
     * @param x     the x target.
     * @param y     the y target.
     * @param z     the z target.
     * @param speed the speed to walk.
     * @return the PathResult.
     */
    public abstract PathResult moveToXYZ(final double x, final double y, final double z, final double speed);

    /**
     * Attempt to move to a specific pos.
     *
     * @param position the position to move to.
     * @param speed    the speed.
     * @return true if successful.
     */
    public abstract boolean tryMoveToBlockPos(final BlockPos position, final double speed);

    /**
     * Get the pathing options
     *
     * @return the pathing options.
     */
    public PathingOptions getPathingOptions() {
        return pathingOptions;
    }

    /**
     * Get the entity of this navigator
     *
     * @return mobentity
     */
    public Mob getOurEntity() {
        return ourEntity;
    }

    /**
     * Gets the desired to go position
     *
     * @return desired go to pos
     */
    public abstract BlockPos getDesiredPos();

    /**
     * Sets the stuck handler for this navigator
     *
     * @param stuckHandler handler to use
     */
    public abstract void setStuckHandler(final IStuckHandler stuckHandler);

    public abstract void setSwimSpeedFactor(double factor);
}
