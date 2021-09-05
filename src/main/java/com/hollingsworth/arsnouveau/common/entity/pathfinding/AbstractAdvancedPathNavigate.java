package com.hollingsworth.arsnouveau.common.entity.pathfinding;

import net.minecraft.entity.MobEntity;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;


public abstract class AbstractAdvancedPathNavigate extends GroundPathNavigator
{
    //  Parent class private members
    protected final MobEntity    ourEntity;
    protected       BlockPos     destination;
    protected       double       walkSpeedFactor = 1.0D;
    protected       BlockPos     originalDestination;

    /**
     * The navigators node costs
     */
    private com.hollingsworth.arsnouveau.common.entity.pathfinding.PathingOptions pathingOptions = new com.hollingsworth.arsnouveau.common.entity.pathfinding.PathingOptions();

    public AbstractAdvancedPathNavigate(
      final MobEntity entityLiving,
      final World worldIn)
    {
        super(entityLiving, worldIn);
        this.ourEntity = mob;
    }

    /**
     * Get the destination from the path.
     *
     * @return the destination position.
     */
    public BlockPos getDestination()
    {
        return destination;
    }

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
     * @param position the position to move to.
     * @param speed the speed.
     * @return true if successful.
     */
    public abstract boolean tryMoveToBlockPos(final BlockPos position, final double speed);

    /**
     * Get the pathing options
     *
     * @return the pathing options.
     */
    public PathingOptions getPathingOptions()
    {
        return pathingOptions;
    }

    /**
     * Get the entity of this navigator
     *
     * @return mobentity
     */
    public MobEntity getOurEntity()
    {
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
