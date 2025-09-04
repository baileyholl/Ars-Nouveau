package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.api.entity.ISummon;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;

public interface IFollowingSummon {

    Level getWorld();

    PathNavigation getPathNav();

    LivingEntity getSummoner();

    Mob getSelfEntity();

    class CopyOwnerTargetGoal<I extends PathfinderMob & IFollowingSummon> extends TargetGoal {

        public CopyOwnerTargetGoal(I creature) {
            super(creature, false);
        }

        /**
         * Returns whether the EntityAIBase should begin execution.
         */
        public boolean canUse() {
            if (!(this.mob instanceof IFollowingSummon summon)) return false;
            LivingEntity summoner = summon.getSummoner();
            if (summoner == null) return false;
            var lastHurtMob = summoner.getLastHurtMob();

            return lastHurtMob != null && summoner != lastHurtMob && !(lastHurtMob instanceof ISummon summon2 && summon2.getOwnerAlt() == summoner);
        }

        /**
         * Execute a one shot task or start executing a continuous task
         */
        public void start() {
            if (mob instanceof IFollowingSummon summon && summon.getSummoner() != null)
                mob.setTarget(summon.getSummoner().getLastHurtMob());
            super.start();
        }
    }

}
