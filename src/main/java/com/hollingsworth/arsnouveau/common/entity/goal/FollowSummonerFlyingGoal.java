package com.hollingsworth.arsnouveau.common.entity.goal;

import com.hollingsworth.arsnouveau.common.entity.IFollowingSummon;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.tags.BlockTags;
import net.minecraft.core.BlockPos;

public class FollowSummonerFlyingGoal extends FollowSummonerGoal{


    public FollowSummonerFlyingGoal(IFollowingSummon mobEntity, LivingEntity owner, double followSpeedIn, float minDistIn, float maxDistIn) {
        super(mobEntity, owner, followSpeedIn, minDistIn, maxDistIn);
    }

    protected boolean canTeleportToBlock(BlockPos pos) {
        BlockState blockstate = this.world.getBlockState(pos);
        return (blockstate.isLadder(this.world, pos, this.summon.getSelfEntity()) || blockstate.is(BlockTags.LEAVES)) && this.world.isEmptyBlock(pos.above()) && this.world.isEmptyBlock(pos.above(2));
    }
}
