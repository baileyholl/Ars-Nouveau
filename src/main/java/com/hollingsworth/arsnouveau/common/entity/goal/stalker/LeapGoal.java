package com.hollingsworth.arsnouveau.common.entity.goal.stalker;

import com.hollingsworth.arsnouveau.common.entity.WildenStalker;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketAnimEntity;
import net.minecraft.entity.ai.goal.Goal;

import java.util.EnumSet;

public class LeapGoal extends Goal {
    WildenStalker stalker;

    public LeapGoal(WildenStalker stalker){
        this.stalker = stalker;
        this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public void startExecuting() {
        stalker.setLeapCooldown(400);
        stalker.addVelocity(0, 2.5, 0);
        stalker.setFlying(true);
        Networking.sendToNearby(stalker.world, stalker, new PacketAnimEntity(stalker.getEntityId(), WildenStalker.Animations.FLY.ordinal()));
    }

    @Override
    public boolean shouldContinueExecuting() {
        return super.shouldContinueExecuting();
    }

    @Override
    public void tick() {
        super.tick();
        if(stalker.timeFlying < 20){
            stalker.addVelocity(0, 0.1, 0);
        }
    }

    @Override
    public boolean shouldExecute() {
        return stalker.getAttackTarget() != null && !stalker.isFlying() && stalker.getLeapCooldown() == 0;
    }
}
