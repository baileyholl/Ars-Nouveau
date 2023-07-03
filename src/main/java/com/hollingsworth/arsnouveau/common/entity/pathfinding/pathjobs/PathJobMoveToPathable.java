package com.hollingsworth.arsnouveau.common.entity.pathfinding.pathjobs;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Path;

import java.util.List;

public class PathJobMoveToPathable extends PathJobMoveToLocation {

    public List<BlockPos> destinations;

    /**
     * Prepares the PathJob for the path finding system.
     *
     * @param world        world the entity is in.
     * @param start        starting location.
     * @param destinations list of acceptable destinations
     * @param range        max search range.
     * @param entity       the entity.
     */
    public PathJobMoveToPathable(Level world, BlockPos start, List<BlockPos> destinations, int range, LivingEntity entity) {
        super(world, start, destinations.size() == 0 ? start : destinations.get(0), range, entity);
        this.destinations = destinations;
    }

    @Override
    protected Path search() {
//        Path path = null;
//        for (BlockPos p : destinations) {
//            this.destination = p;
//            totalNodesVisited = 0;
//            nodesOpen = new PriorityQueue<>(500);
//            nodesVisited = new HashMap<>();
//            path = super.search();
//            if (path.canReach())
//                return path;
//        }
//        destination = destinations.get(0);
        return super.search();
    }
}
