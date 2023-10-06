package com.hollingsworth.arsnouveau.common.entity.pathfinding.pathjobs;

import com.hollingsworth.arsnouveau.common.entity.pathfinding.ModNode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Path;

/**
 * Job that handles moving to a location.
 */
public class PathJobMoveToLocation extends AbstractPathJob {
    public static final float DESTINATION_SLACK_NONE = 0.1F;
    // 1^2 + 1^2 + 1^2 + (epsilon of 0.1F)
    public static final float DESTINATION_SLACK_ADJACENT = (float) Math.sqrt(2f);
    public BlockPos destination;
    // 0 = exact match
    public float destinationSlack = DESTINATION_SLACK_NONE;

    /**
     * Prepares the PathJob for the path finding system.
     *
     * @param world  world the entity is in.
     * @param start  starting location.
     * @param end    target location.
     * @param range  max search range.
     * @param entity the entity.
     */
    public PathJobMoveToLocation(final Level world, final BlockPos start, final BlockPos end, final int range, final LivingEntity entity) {
        super(world, start, end, range, entity);

        this.destination = new BlockPos(end);
    }

    /**
     * Perform the search.
     *
     * @return Path of a path to the given location, a best-effort, or null.
     */
    @Override
    protected Path search() {
        //  Compute destination slack - if the destination point cannot be stood in
        if (getGroundHeight(null, destination) != destination.getY()) {
            destinationSlack = DESTINATION_SLACK_ADJACENT;
        }

        return super.search();
    }

    @Override
    protected BlockPos getPathTargetPos(final ModNode finalNode) {
        return destination;
    }

    @Override
    protected double computeHeuristic(final BlockPos pos) {
        return Math.sqrt(destination.distSqr(pos));
    }

    /**
     * Checks if the target has been reached.
     *
     * @param n Node to test.
     * @return true if has been reached.
     */
    @Override
    protected boolean isAtDestination(final ModNode n) {
        if (destinationSlack <= DESTINATION_SLACK_NONE) {
            return n.pos.getX() == destination.getX()
                    && n.pos.getY() == destination.getY()
                    && n.pos.getZ() == destination.getZ();
        }

        if (n.pos.getY() == destination.getY() - 1) {
            return destination.closerThan(new Vec3i(n.pos.getX(), destination.getY(), n.pos.getZ()), DESTINATION_SLACK_ADJACENT);
        }
        return destination.closerThan(n.pos, DESTINATION_SLACK_ADJACENT);
    }

    /**
     * Calculate the distance to the target.
     *
     * @param n Node to test.
     * @return double of the distance.
     */
    @Override
    protected double getNodeResultScore(final ModNode n) {
        //  For Result Score lower is better
        return destination.distManhattan(n.pos);
    }
}
