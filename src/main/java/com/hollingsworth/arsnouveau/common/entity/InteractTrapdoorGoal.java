package com.hollingsworth.arsnouveau.common.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.TrapDoorBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.GroundPathHelper;
import net.minecraft.util.math.BlockPos;

public class InteractTrapdoorGoal extends Goal {

    protected MobEntity mob;
    protected BlockPos doorPos = BlockPos.ZERO;
    protected boolean hasDoor;
    private boolean passed;
    private float doorOpenDirX;
    private float doorOpenDirZ;

    public InteractTrapdoorGoal(MobEntity p_i1621_1_) {
        this.mob = p_i1621_1_;
        if (!GroundPathHelper.hasGroundPathNavigation(p_i1621_1_)) {
            throw new IllegalArgumentException("Unsupported mob type for DoorInteractGoal");
        }
    }

    protected boolean isOpen() {
        if (!this.hasDoor) {
            return false;
        } else {
            BlockState blockstate = this.mob.level.getBlockState(this.doorPos);
            if (!(blockstate.getBlock() instanceof TrapDoorBlock)) {
                this.hasDoor = false;
                return false;
            } else {
                return blockstate.getValue(TrapDoorBlock.OPEN);
            }
        }
    }

    protected void setOpen(boolean p_195921_1_) {
        if (this.hasDoor) {
            BlockState blockstate = this.mob.level.getBlockState(this.doorPos);
            if (blockstate.getBlock() instanceof TrapDoorBlock) {
                blockstate = blockstate.cycle(TrapDoorBlock.OPEN);
                mob.level.setBlock(doorPos, blockstate, 2);
            }
        }

    }

    public boolean canUse() {
        if (!GroundPathHelper.hasGroundPathNavigation(this.mob)) {
            return false;
        } else if (!this.mob.horizontalCollision) {
            return false;
        } else {
            GroundPathNavigator groundpathnavigator = (GroundPathNavigator)this.mob.getNavigation();
            Path path = groundpathnavigator.getPath();
            if (path != null && !path.isDone() && groundpathnavigator.canOpenDoors()) {
                for(int i = 0; i < Math.min(path.getNextNodeIndex() + 2, path.getNodeCount()); ++i) {
                    PathPoint pathpoint = path.getNode(i);
                    this.doorPos = new BlockPos(pathpoint.x, pathpoint.y, pathpoint.z);
                    if (!(this.mob.distanceToSqr(this.doorPos.getX(), this.mob.getY(), this.doorPos.getZ()) > 2.25D)) {
                        this.hasDoor = isWoodenTrapdoor();
                        if (this.hasDoor) {
                            return true;
                        }
                    }
                }

                this.doorPos = this.mob.blockPosition().above();
                this.hasDoor = isWoodenTrapdoor();
                return this.hasDoor;
            } else {
                return false;
            }
        }
    }

    public boolean isWoodenTrapdoor(){
        return mob.level.getBlockState(doorPos).getBlock() instanceof TrapDoorBlock && mob.level.getBlockState(doorPos).getMaterial() == Material.WOOD;
    }



    public boolean canContinueToUse() {
        return !this.passed;
    }

    public void start() {
        this.passed = false;
        this.doorOpenDirX = (float)((double)this.doorPos.getX() + 0.5D - this.mob.getX());
        this.doorOpenDirZ = (float)((double)this.doorPos.getZ() + 0.5D - this.mob.getZ());
    }

    public void tick() {
        float f = (float)((double)this.doorPos.getX() + 0.5D - this.mob.getX());
        float f1 = (float)((double)this.doorPos.getZ() + 0.5D - this.mob.getZ());
        float f2 = this.doorOpenDirX * f + this.doorOpenDirZ * f1;
        if (f2 < 0.0F) {
            this.passed = true;
        }

    }
}
