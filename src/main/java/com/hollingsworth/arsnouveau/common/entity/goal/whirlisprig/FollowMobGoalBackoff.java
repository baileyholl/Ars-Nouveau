package com.hollingsworth.arsnouveau.common.entity.goal.whirlisprig;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.FollowMobGoal;

public class FollowMobGoalBackoff extends FollowMobGoal {
    float chance;
    Mob mob;
    public FollowMobGoalBackoff(Mob mob, double speed, float stopDistance, float areaSize, float chance) {
        super(mob, speed, stopDistance, areaSize);
        this.chance = chance;
        this.mob = mob;
    }

    @Override
    public boolean canUse() {
        return this.mob.level.random.nextFloat() <= chance && super.canUse();
    }
}
