package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.entity.goal.ExtendedRangeGoal;
import net.minecraft.entity.MobEntity;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

public class MoveToGoal extends ExtendedRangeGoal {
    MobEntity mobEntity;
    public BlockPos adjustedPath;
    public MoveToGoal(MobEntity mobEntity, int ticksPerDistance) {
        super(ticksPerDistance);
        this.mobEntity = mobEntity;
    }

    @Override
    public void stop() {
        adjustedPath = null;
    }

    @Override
    public void start() {
        adjustedPath = null;
    }

    public boolean closeEnoughResetExtension(Vector3d target, double distance){
        return BlockUtil.distanceFrom(mobEntity.position.add(0.5, 0.5, 0.5), new Vector3d(target.x + 0.5, target.y + 0.5, target.z + 0.5)) <= distance + this.extendedRange ||
                (adjustedPath != null && BlockUtil.distanceFrom(mobEntity.position.add(0.5, 0.5, 0.5), new Vector3d(adjustedPath.getX() + 0.5, adjustedPath.getY(), adjustedPath.getZ() + 0.5)) <= distance + this.extendedRange);
    }

    public boolean closeEnoughResetExtension(BlockPos target, double distance){
        if(target == null)
            return false;
        boolean closeEnough = closeEnoughResetExtension(new Vector3d(target.getX(), target.getY(), + target.getZ()), distance);
        if(closeEnough)
            super.resetExtendedRange();
        return closeEnough;
    }

    public boolean moveTo(BlockPos target, double speed){
        for(int i = 0; i < 3; i++) {
            Path basePath = mobEntity.getNavigation().createPath(target, 1 + i);
            if(basePath != null && basePath.canReach()){
                return mobEntity.getNavigation().moveTo(basePath, speed);
            }else{
                List<Direction> preferred = getClosestDirections(new Vector3d(target.getX(), target.getY(), target.getZ()));
                for(Direction d : preferred){
                    BlockPos pos = target.relative(d);
                    if(mobEntity.level.getBlockState(pos).getMaterial().blocksMotion())
                        continue;
                    Path adjustedPath = mobEntity.getNavigation().createPath(pos, 1 + i);
                    if(adjustedPath != null && adjustedPath.canReach()) {
                        this.adjustedPath = pos;
                        return mobEntity.getNavigation().moveTo(adjustedPath, speed);
                    }
                }
            }
        }
        return false;
    }

    public List<Direction> getClosestDirections(Vector3d target){
        Direction[] directions = Direction.values();
        List<Direction> preferred = Arrays.asList(directions);
        preferred.sort((a, b) -> {
            Vector3i aVec = a.getNormal();
            Vector3d adjustedA = target.add(aVec.getX(), aVec.getY(), aVec.getZ());
            Vector3i bVec = b.getNormal();
            Vector3d adjustedB = target.add(bVec.getX(), bVec.getY(), bVec.getZ());
            return BigDecimal.valueOf(BlockUtil.distanceFrom(mobEntity.position, adjustedA))
                    .compareTo(BigDecimal.valueOf(BlockUtil.distanceFrom(mobEntity.position, adjustedB)));
        });
        return preferred;
    }


}
