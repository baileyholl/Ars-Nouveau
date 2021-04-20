package com.hollingsworth.arsnouveau.common.entity.goal.sylph;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.FollowMobGoal;

public class FollowMobGoalBackoff extends FollowMobGoal {
    float chance;
    MobEntity mob;
    public FollowMobGoalBackoff(MobEntity mob, double speed, float stopDistance, float areaSize, float chance) {
        super(mob, speed, stopDistance, areaSize);
        this.chance = chance;
        this.mob = mob;
    }

    @Override
    public boolean canUse() {
        return this.mob.level.random.nextFloat() <= chance && super.canUse();
    }
}
