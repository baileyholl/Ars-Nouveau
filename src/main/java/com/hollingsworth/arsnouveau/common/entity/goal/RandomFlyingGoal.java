package com.hollingsworth.arsnouveau.common.entity.goal;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.tags.BlockTags;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Iterator;

public class RandomFlyingGoal extends WaterAvoidingRandomStrollGoal {
    public RandomFlyingGoal(PathfinderMob p_i47413_1_, double p_i47413_2_) {
        super(p_i47413_1_, p_i47413_2_);
    }

    @Nullable
    protected Vec3 getPosition() {
        Vec3 vec3d = null;
        if (this.mob.isInWater()) {
            vec3d = RandomPos.getLandPos(this.mob, 15, 15);
        }

        if (this.mob.getRandom().nextFloat() >= this.probability) {
            vec3d = this.getTreePos();
        }

        return vec3d == null ? super.getPosition() : vec3d;
    }

    @Nullable
    private Vec3 getTreePos() {
        BlockPos blockpos = new BlockPos(this.mob.blockPosition());
        BlockPos.MutableBlockPos blockpos$mutable = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos blockpos$mutable1 = new BlockPos.MutableBlockPos();
        Iterable<BlockPos> iterable = BlockPos.betweenClosed(Mth.floor(this.mob.getX() - 3.0D), Mth.floor(this.mob.getY() - 6.0D), Mth.floor(this.mob.getZ() - 3.0D), Mth.floor(this.mob.getX() + 3.0D), Mth.floor(this.mob.getY() + 6.0D), Mth.floor(this.mob.getZ() + 3.0D));
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

        return new Vec3(blockpos1.getX(),blockpos1.getY(),blockpos1.getZ());
    }
}
