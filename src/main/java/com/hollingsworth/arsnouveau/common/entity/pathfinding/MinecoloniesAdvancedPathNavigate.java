package com.hollingsworth.arsnouveau.common.entity.pathfinding;

import com.hollingsworth.arsnouveau.common.entity.pathfinding.pathjobs.AbstractPathJob;
import com.hollingsworth.arsnouveau.common.entity.pathfinding.pathjobs.PathJobMoveAwayFromLocation;
import com.hollingsworth.arsnouveau.common.entity.pathfinding.pathjobs.PathJobMoveToLocation;
import com.hollingsworth.arsnouveau.common.entity.pathfinding.pathjobs.PathJobMoveToPathable;
import com.hollingsworth.arsnouveau.common.util.Log;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * Minecolonies async PathNavigate.
 */
public class MinecoloniesAdvancedPathNavigate extends AbstractAdvancedPathNavigate {
    private static final double ON_PATH_SPEED_MULTIPLIER = 1.3D;
    public static final double MIN_Y_DISTANCE = 0.001;
    public static final int MAX_SPEED_ALLOWED = 2;
    public static final double MIN_SPEED_ALLOWED = 0.1;

    private PathResult<AbstractPathJob> pathResult;

    /**
     * The world time when a path was added.
     */
    private long pathStartTime = 0;

    /**
     * Spawn pos of minecart.
     */
    private BlockPos spawnedPos = BlockPos.ZERO;

    /**
     * Desired position to reach
     */
    private BlockPos desiredPos;

    /**
     * Timeout for the desired pos, resets when its no longer wanted
     */
    private int desiredPosTimeout = 0;

    /**
     * The stuck handler to use
     */
    private IStuckHandler stuckHandler;

    /**
     * Whether we did set sneaking
     */
    private boolean isSneaking = true;

    /**
     * Speed factor for swimming
     */
    private double swimSpeedFactor = 1.0;

    /**
     * Instantiates the navigation of an ourEntity.
     *
     * @param entity the ourEntity.
     * @param world  the world it is in.
     */
    public MinecoloniesAdvancedPathNavigate(final Mob entity, final Level world) {
        super(entity, world);

        entity.moveControl = new MovementHandler(entity);
        this.nodeEvaluator = new WalkNodeEvaluator();
        this.nodeEvaluator.setCanPassDoors(true);
        getPathingOptions().setEnterDoors(true);
        this.nodeEvaluator.setCanOpenDoors(true);
        getPathingOptions().setCanOpenDoors(true);
        this.nodeEvaluator.setCanFloat(true);
        getPathingOptions().setCanSwim(true);

        stuckHandler = PathingStuckHandler.createStuckHandler().withTeleportSteps(6).withTeleportOnFullStuck();
    }

    @Override
    public BlockPos getDestination() {
        return destination;
    }


    @Nullable
    public PathResult moveAwayFromXYZ(final BlockPos avoid, final double range, final double speedFactor, final boolean safeDestination)
    {
        final BlockPos start = AbstractPathJob.prepareStart(ourEntity);

        return setPathJob(new PathJobMoveAwayFromLocation(ourEntity.level,
                start,
                avoid,
                (int) range,
                (int) ourEntity.getAttribute(Attributes.FOLLOW_RANGE).getValue(),
                ourEntity), null, speedFactor);
    }


    public PathResult setPathJob(
            final AbstractPathJob job,
            final BlockPos dest,
            final double speedFactor) {
        stop();

        this.destination = dest;
        this.originalDestination = dest;
        if (dest != null) {
            desiredPos = dest;
            desiredPosTimeout = 50 * 20;
        }
        this.walkSpeedFactor = speedFactor;

        if (speedFactor > MAX_SPEED_ALLOWED || speedFactor < MIN_SPEED_ALLOWED) {
            Log.getLogger().error("Tried to set a bad speed:" + speedFactor + " for entity:" + ourEntity, new Exception());
            return null;
        }

        job.setPathingOptions(getPathingOptions());
        pathResult = job.getResult();
        pathResult.startJob(Pathfinding.getExecutor());
        return pathResult;
    }

    @Override
    public boolean isDone() {
        return (pathResult == null || pathResult.isDone() && pathResult.getStatus() != PathFindingStatus.CALCULATION_COMPLETE) && super.isDone();
    }

    @Override
    public void tick() {
        if (desiredPosTimeout > 0) {
            if (--desiredPosTimeout <= 0) {
                desiredPos = null;
            }
        }

        if (pathResult != null) {
            if (!pathResult.isDone()) {
                return;
            } else if (pathResult.getStatus() == PathFindingStatus.CALCULATION_COMPLETE) {
                processCompletedCalculationResult();
            }
        }

        int oldIndex = this.isDone() ? 0 : this.getPath().getNextNodeIndex();

        if (isSneaking) {
            isSneaking = false;
            mob.setShiftKeyDown(false);
        }
        this.ourEntity.setYya(0);
        if (handleLadders(oldIndex)) {
            followThePath();
            stuckHandler.checkStuck(this);
            return;
        }
        if (handleRails()) {
            stuckHandler.checkStuck(this);
            return;
        }

        ++this.tick;
        if (this.hasDelayedRecomputation) {
            this.recomputePath();
        }

        // The following block replaces mojangs super.tick(). Why you may ask? Because it's broken, that's why.
        // The moveHelper won't move up if standing in a block with an empty bounding box (put grass, 1 layer snow, mushroom in front of a solid block and have them try jump up).
        if (!this.isDone()) {
            if (this.canUpdatePath()) {
                this.followThePath();
            } else if (this.path != null && !this.path.isDone()) {
                Vec3 vector3d = this.getTempMobPos();
                Vec3 vector3d1 = this.path.getNextEntityPos(this.mob);
                if (vector3d.y > vector3d1.y && !this.mob.onGround() && Mth.floor(vector3d.x) == Mth.floor(vector3d1.x) && Mth.floor(vector3d.z) == Mth.floor(vector3d1.z)) {
                    this.path.advance();
                }
            }

            DebugPackets.sendPathFindingPacket(this.level, this.mob, this.path, this.maxDistanceToWaypoint);
            if (!this.isDone()) {
                Vec3 vector3d2 = this.path.getNextEntityPos(this.mob);
                BlockPos blockpos = BlockPos.containing(vector3d2);
                if (level.isLoaded(blockpos)) {
                    this.mob.getMoveControl()
                            .setWantedPosition(vector3d2.x,
                                    this.level.getBlockState(blockpos.below()).isAir() ? vector3d2.y : getSmartGroundY(this.level, blockpos),
                                    vector3d2.z,
                                    this.speedModifier);
                }
            }
        }
        // End of super.tick.

        if (pathResult != null && isDone()) {
            pathResult.setStatus(PathFindingStatus.COMPLETE);
            pathResult = null;
        }

        stuckHandler.checkStuck(this);
    }

    /**
     * Similar to WalkNodeProcessor.getGroundY but not broken.
     * This checks if the block below the position we're trying to move to reaches into the block above, if so, it has to aim a little bit higher.
     *
     * @param world the world.
     * @param pos   the position to check.
     * @return the next y level to go to.
     */
    public static double getSmartGroundY(final BlockGetter world, final BlockPos pos) {
        final BlockPos blockpos = pos.below();
        final VoxelShape voxelshape = world.getBlockState(blockpos).getCollisionShape(world, blockpos);
        if (voxelshape.isEmpty() || voxelshape.max(Direction.Axis.Y) < 1.0) {
            return pos.getY();
        }
        return blockpos.getY() + voxelshape.max(Direction.Axis.Y);
    }

    public PathResult moveToXYZ(final double x, final double y, final double z, final double speedFactor) {
        final int newX = Mth.floor(x);
        final int newY = (int) y;
        final int newZ = Mth.floor(z);

        if (pathResult != null && pathResult.getJob() instanceof PathJobMoveToLocation &&
                (
                        pathResult.isComputing()
                                || (destination != null && destination.equals(new BlockPos(newX, newY, newZ)))
                                || (originalDestination != null && originalDestination.equals(new BlockPos(newX, newY, newZ)))
                )
        ) {
            return pathResult;
        }

        final BlockPos start = AbstractPathJob.prepareStart(ourEntity);
        desiredPos = new BlockPos(newX, newY, newZ);

        return setPathJob(
                new PathJobMoveToLocation(ourEntity.level,
                        start,
                        desiredPos,
                        (int) ourEntity.getAttribute(Attributes.FOLLOW_RANGE).getValue(),
                        ourEntity),
                desiredPos, speedFactor);
    }

    public PathResult moveToClosestPosition(List<BlockPos> positions, double speedFactor) {
        if (pathResult != null && pathResult.getJob() instanceof PathJobMoveToLocation && (
                pathResult.isComputing()
                        || (destination != null && positions.contains(destination)))
                || (originalDestination != null && positions.contains(originalDestination)
        )
        ) {
            return pathResult;
        }
        final BlockPos start = AbstractPathJob.prepareStart(ourEntity);
        return setPathJob(new PathJobMoveToPathable(ourEntity.level, start, positions,
                (int) ourEntity.getAttribute(Attributes.FOLLOW_RANGE).getValue(),
                ourEntity), null, speedFactor);
    }

    @Override
    public boolean tryMoveToBlockPos(final BlockPos pos, final double speedFactor) {
        if(pos == null){
            return false;
        }
        moveToXYZ(pos.getX(), pos.getY(), pos.getZ(), speedFactor);
        return true;
    }

    @Override
    protected PathFinder createPathFinder(final int p_179679_1_) {
        return null;
    }

    @Override
    protected boolean canUpdatePath() {
        return true;
    }

    @Override
    protected Vec3 getTempMobPos() {
        return this.ourEntity.position();
    }

    @Override
    public Path createPath(final BlockPos pos, final int p_179680_2_) {
        //Because this directly returns Path we can't do it async.
        return null;
    }

    @Override
    protected boolean canMoveDirectly(Vec3 start, Vec3 end) {
        //  special path blocks go into the false as !specialPathBlocks
        return  super.canMoveDirectly(start, end);
    }

    public double getSpeedFactor() {
        if (ourEntity.isInWater()) {
            speedModifier = walkSpeedFactor * swimSpeedFactor;
            return speedModifier;
        }

        speedModifier = walkSpeedFactor;
        return walkSpeedFactor;
    }

    @Override
    public void setSpeedModifier(final double speedFactor) {
        if (speedFactor > MAX_SPEED_ALLOWED || speedFactor < MIN_SPEED_ALLOWED) {
            Log.getLogger().error("Tried to set a bad speed:" + speedFactor + " for entity:" + ourEntity, new Exception());
            return;
        }
        walkSpeedFactor = speedFactor;
    }

    /**
     * Deprecated - try to use BlockPos instead
     */
    @Override
    public boolean moveTo(final double x, final double y, final double z, final double speedFactor) {
        if (x == 0 && y == 0 && z == 0) {
            return false;
        }

        moveToXYZ(x, y, z, speedFactor);
        return true;
    }

    @Override
    public boolean moveTo(final Entity entityIn, final double speedFactor) {
        return tryMoveToBlockPos(entityIn.blockPosition(), speedFactor);
    }

    // Removes stupid vanilla stuff, causing our pathpoints to occasionally be replaced by vanilla ones.
    @Override
    protected void trimPath() {
    }

    @Override
    public boolean moveTo(final Path path, final double speedFactor) {
        if (path == null) {
            stop();
            return false;
        }
        pathStartTime = level.getGameTime();
        return super.moveTo(convertPath(path), speedFactor);
    }

    /**
     * Converts the given path to a minecolonies path if needed.
     *
     * @param path given path
     * @return resulting path
     */
    private Path convertPath(final Path path) {
        final int pathLength = path.getNodeCount();
        Path tempPath = null;
        if (pathLength > 0 && !(path.getNode(0) instanceof PathPointExtended)) {
            //  Fix vanilla PathPoints to be PathPointExtended
            final PathPointExtended[] newPoints = new PathPointExtended[pathLength];

            for (int i = 0; i < pathLength; ++i) {
                final Node point = path.getNode(i);
                if (!(point instanceof PathPointExtended)) {
                    newPoints[i] = new PathPointExtended(new BlockPos(point.x, point.y, point.z));
                } else {
                    newPoints[i] = (PathPointExtended) point;
                }
            }

            tempPath = new Path(Arrays.asList(newPoints), path.getTarget(), path.canReach());

            final PathPointExtended finalPoint = newPoints[pathLength - 1];
            destination = new BlockPos(finalPoint.x, finalPoint.y, finalPoint.z);
        }

        return tempPath == null ? path : tempPath;
    }

    private boolean processCompletedCalculationResult() {
        pathResult.getJob().synchToClient(mob);
        moveTo(pathResult.getPath(), getSpeedFactor());
        if (pathResult != null) {
            pathResult.setStatus(PathFindingStatus.IN_PROGRESS_FOLLOWING);
        }
        return false;
    }

    private boolean handleLadders(int oldIndex) {
        //  Ladder Workaround
        if (!this.isDone()) {
            @NotNull final PathPointExtended pEx = (PathPointExtended) this.getPath().getNode(this.getPath().getNextNodeIndex());
            final PathPointExtended pExNext = getPath().getNodeCount() > this.getPath().getNextNodeIndex() + 1
                    ? (PathPointExtended) this.getPath()
                    .getNode(this.getPath()
                            .getNextNodeIndex() + 1) : null;


            final BlockPos pos = new BlockPos(pEx.x, pEx.y, pEx.z);
            if (pEx.isOnLadder() && pExNext != null && (pEx.y != pExNext.y || mob.getY() > pEx.y) && level.getBlockState(pos).isLadder(level, pos, ourEntity)) {
                return handlePathPointOnLadder(pEx);
            } else if (ourEntity.isInWater()) {
                return handleEntityInWater(oldIndex, pEx);
            } else if (level.random.nextInt(20) == 0) {
                if (!pEx.isOnLadder() && pExNext != null && pExNext.isOnLadder()) {
                    speedModifier = getSpeedFactor() / 4.0;
                } else if (false
                //WorkerUtil.isPathBlock(level.getBlockState(findBlockUnderEntity(ourEntity)).getBlock())
                ) {
                    speedModifier = ON_PATH_SPEED_MULTIPLIER * getSpeedFactor();
                } else {
                    speedModifier = getSpeedFactor();
                }
            }
        }
        return false;
    }

    /**
     * Determine what block the entity stands on
     *
     * @param parEntity the entity that stands on the block
     * @return the Blockstate.
     */
    private BlockPos findBlockUnderEntity(final Entity parEntity) {
        int blockX = (int) Math.round(parEntity.getX());
        int blockY = Mth.floor(parEntity.getY() - 0.2D);
        int blockZ = (int) Math.round(parEntity.getZ());
        return new BlockPos(blockX, blockY, blockZ);
    }

    /**
     * Handle rails navigation.
     *
     * @return true if block.
     */
    private boolean handleRails() {
        if (!this.isDone()) {
            final PathPointExtended pEx = (PathPointExtended) this.getPath().getNode(this.getPath().getNextNodeIndex());
            PathPointExtended pExNext = getPath().getNodeCount() > this.getPath().getNextNodeIndex() + 1
                    ? (PathPointExtended) this.getPath()
                    .getNode(this.getPath()
                            .getNextNodeIndex() + 1) : null;

            if (pExNext != null && pEx.x == pExNext.x && pEx.z == pExNext.z) {
                pExNext = getPath().getNodeCount() > this.getPath().getNextNodeIndex() + 2
                        ? (PathPointExtended) this.getPath()
                        .getNode(this.getPath()
                                .getNextNodeIndex() + 2) : null;
            }

            if (pEx.isOnRails() || pEx.isRailsExit()) {
                return handlePathOnRails(pEx, pExNext);
            }
        }
        return false;
    }

    /**
     * Handle pathing on rails.
     *
     * @param pEx     the current path point.
     * @param pExNext the next path point.
     * @return if go to next point.
     */
    private boolean handlePathOnRails(final PathPointExtended pEx, final PathPointExtended pExNext) {
        return false;
    }

    private boolean handlePathPointOnLadder(final PathPointExtended pEx) {
        Vec3 vec3 = this.getPath().getNextEntityPos(this.ourEntity);
        final BlockPos entityPos = BlockPos.containing(this.ourEntity.position());
        if (vec3.distanceToSqr(ourEntity.getX(), vec3.y, ourEntity.getZ()) < Math.random() * 0.1) {
            //This way he is less nervous and gets up the ladder
            double newSpeed = 0.3;
            switch (pEx.getLadderFacing()) {
                //  Any of these values is climbing, so adjust our direction of travel towards the ladder
                case NORTH -> vec3 = vec3.add(0, 0, 1);
                case SOUTH -> vec3 = vec3.add(0, 0, -1);
                case WEST -> vec3 = vec3.add(1, 0, 0);
                case EAST -> vec3 = vec3.add(-1, 0, 0);
                case UP -> vec3 = vec3.add(0, 1, 0);

                //  Any other value is going down, so lets not move at all
                default -> {
                    newSpeed = 0;
                    mob.setShiftKeyDown(true);
                    isSneaking = true;
                }
            }

            if (newSpeed > 0) {
                if (!(level.getBlockState(ourEntity.blockPosition()).getBlock() instanceof LadderBlock)) {
                    this.ourEntity.setDeltaMovement(this.ourEntity.getDeltaMovement().add(0, 0.1D, 0));
                }
                this.ourEntity.getMoveControl().setWantedPosition(vec3.x, vec3.y, vec3.z, newSpeed);
            } else {
                if (level.getBlockState(entityPos.below()).isLadder(level, entityPos.below(), ourEntity)) {
                    this.ourEntity.setYya(-0.5f);
                } else {
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    private boolean handleEntityInWater(int oldIndex, final PathPointExtended pEx) {
        //  Prevent shortcuts when swimming
        final int curIndex = this.getPath().getNextNodeIndex();
        if (curIndex > 0
                && (curIndex + 1) < this.getPath().getNodeCount()
                && this.getPath().getNode(curIndex - 1).y != pEx.y) {
            //  Work around the initial 'spin back' when dropping into water
            oldIndex = curIndex + 1;
        }

        this.getPath().setNextNodeIndex(oldIndex);

        Vec3 Vector3d = this.getPath().getNextEntityPos(this.ourEntity);

        if (Vector3d.distanceToSqr(new Vec3(ourEntity.getX(), Vector3d.y, ourEntity.getZ())) < 0.1
                && Math.abs(ourEntity.getY() - Vector3d.y) < 0.5) {
            this.getPath().advance();
            if (this.isDone()) {
                return true;
            }

            Vector3d = this.getPath().getNextEntityPos(this.ourEntity);
        }

        this.ourEntity.getMoveControl().setWantedPosition(Vector3d.x, Vector3d.y, Vector3d.z, getSpeedFactor());
        return false;
    }

    @Override
    protected void followThePath() {
        getSpeedFactor();
        final int curNode = path.getNextNodeIndex();
        final int curNodeNext = curNode + 1;
        if (curNodeNext < path.getNodeCount()) {
            if (!(path.getNode(curNode) instanceof PathPointExtended)) {
                path = convertPath(path);
            }

            final PathPointExtended pEx = (PathPointExtended) path.getNode(curNode);
            final PathPointExtended pExNext = (PathPointExtended) path.getNode(curNodeNext);

            //  If current node is bottom of a ladder, then stay on this node until
            //  the ourEntity reaches the bottom, otherwise they will try to head out early
            if (pEx.isOnLadder() && pEx.getLadderFacing() == Direction.DOWN
                    && !pExNext.isOnLadder()) {
                final Vec3 vec3 = getTempMobPos();
                if ((vec3.y - (double) pEx.y) < MIN_Y_DISTANCE) {
                    this.path.setNextNodeIndex(curNodeNext);
                }
                return;
            }
        }

        this.maxDistanceToWaypoint = 0.5F;
        boolean wentAhead = false;
        boolean isTracking = AbstractPathJob.DEBUG_DRAW;// AbstractPathJob.trackingMap.containsValue(ourEntity.getUUID());

        final HashSet<BlockPos> reached = new HashSet<>();
        // Look at multiple points, incase we're too fast
        for (int i = this.path.getNextNodeIndex(); i < Math.min(this.path.getNodeCount(), this.path.getNextNodeIndex() + 4); i++) {
            Vec3 next = this.path.getEntityPosAtNode(this.mob, i);
            if (Math.abs(this.mob.getX() - next.x) < (double) this.maxDistanceToWaypoint - Math.abs(this.mob.getY() - (next.y)) * 0.1
                    && Math.abs(this.mob.getZ() - next.z) < (double) this.maxDistanceToWaypoint - Math.abs(this.mob.getY() - (next.y)) * 0.1 &&
                    Math.abs(this.mob.getY() - next.y) <= 1.0D) {
                this.path.advance();
                wentAhead = true;

                if (isTracking) {
                    final Node point = path.getNode(i);
                    reached.add(new BlockPos(point.x, point.y, point.z));
                }
            }
        }

        if (isTracking) {
            AbstractPathJob.synchToClient(reached, ourEntity);
            reached.clear();
        }

        if (path.isDone()) {
            onPathFinish();
            return;
        }

        if (wentAhead) {
            return;
        }

        if (curNode >= path.getNodeCount() || curNode <= 1) {
            return;
        }

        // Check some past nodes case we fell behind.
        final Vec3 curr = this.path.getEntityPosAtNode(this.mob, curNode - 1);
        final Vec3 next = this.path.getEntityPosAtNode(this.mob, curNode);

        if (mob.position().distanceTo(curr) >= 2.0 && mob.position().distanceTo(next) >= 2.0) {
            int currentIndex = curNode - 1;
            while (currentIndex > 0) {
                final Vec3 tempoPos = this.path.getEntityPosAtNode(this.mob, currentIndex);
                if (mob.position().distanceTo(tempoPos) <= 1.0) {
                    this.path.setNextNodeIndex(currentIndex);
                } else if (isTracking) {
                    reached.add(BlockPos.containing(tempoPos.x, tempoPos.y, tempoPos.z));
                }
                currentIndex--;
            }
        }

        if (isTracking) {
            AbstractPathJob.synchToClient(reached, ourEntity);
            reached.clear();
        }
    }


    /**
     * Called upon reaching the path end, reset values
     */
    private void onPathFinish() {
        stop();
    }

    public void recomputePath() {
    }

    /**
     * Don't let vanilla rapidly discard paths, set a timeout before its allowed to use stuck.
     */
    @Override
    protected void doStuckDetection(final Vec3 positionVec3) {
        // Do nothing, unstuck is checked on tick, not just when we have a path
    }

    @Override
    public void stop() {
        if (pathResult != null) {
            pathResult.cancel();
            pathResult.setStatus(PathFindingStatus.CANCELLED);
            pathResult = null;
        }

        destination = null;
        super.stop();
    }

    @Override
    public void setCanFloat(boolean canSwim) {
        super.setCanFloat(canSwim);
        getPathingOptions().setCanSwim(canSwim);
    }

    public BlockPos getDesiredPos() {
        return desiredPos;
    }

    /**
     * Sets the stuck handler
     *
     * @param stuckHandler handler to set
     */
    @Override
    public void setStuckHandler(final IStuckHandler stuckHandler) {
        this.stuckHandler = stuckHandler;
    }

    @Override
    public void setSwimSpeedFactor(final double factor) {
        this.swimSpeedFactor = factor;
    }
}
