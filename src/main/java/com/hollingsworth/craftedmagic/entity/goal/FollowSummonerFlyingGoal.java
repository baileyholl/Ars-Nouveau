package com.hollingsworth.craftedmagic.entity.goal;

import com.hollingsworth.craftedmagic.api.ISummon;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;

public class FollowSummonerFlyingGoal extends FollowSummonerGoal{


    public FollowSummonerFlyingGoal(ISummon mobEntity, LivingEntity owner, double followSpeedIn, float minDistIn, float maxDistIn) {
        super(mobEntity, owner, followSpeedIn, minDistIn, maxDistIn);
    }

    protected boolean canTeleportToBlock(BlockPos pos) {
        BlockState blockstate = this.world.getBlockState(pos);
        return (blockstate.func_215682_a(this.world, pos, this.summon.getSelfEntity()) || blockstate.isIn(BlockTags.LEAVES)) && this.world.isAirBlock(pos.up()) && this.world.isAirBlock(pos.up(2));
    }
}
