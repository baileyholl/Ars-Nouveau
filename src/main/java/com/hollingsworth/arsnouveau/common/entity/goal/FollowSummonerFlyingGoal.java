package com.hollingsworth.arsnouveau.common.entity.goal;

import com.hollingsworth.arsnouveau.api.IFollowingSummon;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;

public class FollowSummonerFlyingGoal extends FollowSummonerGoal{


    public FollowSummonerFlyingGoal(IFollowingSummon mobEntity, LivingEntity owner, double followSpeedIn, float minDistIn, float maxDistIn) {
        super(mobEntity, owner, followSpeedIn, minDistIn, maxDistIn);
    }

    protected boolean canTeleportToBlock(BlockPos pos) {
        BlockState blockstate = this.world.getBlockState(pos);
        return (blockstate.isLadder(this.world, pos, this.summon.getSelfEntity()) || blockstate.isIn(BlockTags.LEAVES)) && this.world.isAirBlock(pos.up()) && this.world.isAirBlock(pos.up(2));
    }
}
