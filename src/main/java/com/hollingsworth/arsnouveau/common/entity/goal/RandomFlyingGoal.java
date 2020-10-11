package com.hollingsworth.arsnouveau.common.entity.goal;

import net.minecraft.block.Block;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;
import java.util.Iterator;

public class RandomFlyingGoal extends WaterAvoidingRandomWalkingGoal {
    public RandomFlyingGoal(CreatureEntity p_i47413_1_, double p_i47413_2_) {
        super(p_i47413_1_, p_i47413_2_);
    }

    @Nullable
    protected Vec3d getPosition() {
        Vec3d vec3d = null;
        if (this.creature.isInWater()) {
            vec3d = RandomPositionGenerator.getLandPos(this.creature, 15, 15);
        }

        if (this.creature.getRNG().nextFloat() >= this.probability) {
            vec3d = this.getTreePos();
        }

        return vec3d == null ? super.getPosition() : vec3d;
    }

    @Nullable
    private Vec3d getTreePos() {
        BlockPos blockpos = new BlockPos(this.creature);
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
        BlockPos.Mutable blockpos$mutable1 = new BlockPos.Mutable();
        Iterable<BlockPos> iterable = BlockPos.getAllInBoxMutable(MathHelper.floor(this.creature.getPosX() - 3.0D), MathHelper.floor(this.creature.getPosY() - 6.0D), MathHelper.floor(this.creature.getPosZ() - 3.0D), MathHelper.floor(this.creature.getPosX() + 3.0D), MathHelper.floor(this.creature.getPosY() + 6.0D), MathHelper.floor(this.creature.getPosZ() + 3.0D));
        Iterator iterator = iterable.iterator();

        BlockPos blockpos1;
        while(true) {
            if (!iterator.hasNext()) {
                return null;
            }

            blockpos1 = (BlockPos)iterator.next();
            if (!blockpos.equals(blockpos1)) {
                Block block = this.creature.world.getBlockState(blockpos$mutable1.setPos(blockpos1).move(Direction.DOWN)).getBlock();
                boolean flag = block instanceof LeavesBlock || block.isIn(BlockTags.LOGS);
                if (flag && this.creature.world.isAirBlock(blockpos1) && this.creature.world.isAirBlock(blockpos$mutable.setPos(blockpos1).move(Direction.UP))) {
                    break;
                }
            }
        }

        return new Vec3d(blockpos1);
    }
}
