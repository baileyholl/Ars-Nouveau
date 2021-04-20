package com.hollingsworth.arsnouveau.common.entity.goal;

import net.minecraft.block.Block;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nullable;
import java.util.Iterator;

public class RandomFlyingGoal extends WaterAvoidingRandomWalkingGoal {
    public RandomFlyingGoal(CreatureEntity p_i47413_1_, double p_i47413_2_) {
        super(p_i47413_1_, p_i47413_2_);
    }

    @Nullable
    protected Vector3d getPosition() {
        Vector3d vec3d = null;
        if (this.mob.isInWater()) {
            vec3d = RandomPositionGenerator.getLandPos(this.mob, 15, 15);
        }

        if (this.mob.getRandom().nextFloat() >= this.probability) {
            vec3d = this.getTreePos();
        }

        return vec3d == null ? super.getPosition() : vec3d;
    }

    @Nullable
    private Vector3d getTreePos() {
        BlockPos blockpos = new BlockPos(this.mob.blockPosition());
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
        BlockPos.Mutable blockpos$mutable1 = new BlockPos.Mutable();
        Iterable<BlockPos> iterable = BlockPos.betweenClosed(MathHelper.floor(this.mob.getX() - 3.0D), MathHelper.floor(this.mob.getY() - 6.0D), MathHelper.floor(this.mob.getZ() - 3.0D), MathHelper.floor(this.mob.getX() + 3.0D), MathHelper.floor(this.mob.getY() + 6.0D), MathHelper.floor(this.mob.getZ() + 3.0D));
        Iterator iterator = iterable.iterator();

        BlockPos blockpos1;
        while(true) {
            if (!iterator.hasNext()) {
                return null;
            }

            blockpos1 = (BlockPos)iterator.next();
            if (!blockpos.equals(blockpos1)) {
                Block block = this.mob.level.getBlockState(blockpos$mutable1.set(blockpos1).move(Direction.DOWN)).getBlock();
                boolean flag = block instanceof LeavesBlock || block.is(BlockTags.LOGS);
                if (flag && this.mob.level.isEmptyBlock(blockpos1) && this.mob.level.isEmptyBlock(blockpos$mutable.set(blockpos1).move(Direction.UP))) {
                    break;
                }
            }
        }

        return new Vector3d(blockpos1.getX(),blockpos1.getY(),blockpos1.getZ());
    }
}
