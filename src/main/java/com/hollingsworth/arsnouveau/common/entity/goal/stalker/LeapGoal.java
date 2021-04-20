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
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public void start() {
        stalker.setLeapCooldown(400);
        stalker.push(0, 2.5, 0);
        stalker.setFlying(true);
        Networking.sendToNearby(stalker.level, stalker, new PacketAnimEntity(stalker.getId(), WildenStalker.Animations.FLY.ordinal()));
    }

    @Override
    public boolean canContinueToUse() {
        return super.canContinueToUse();
    }

    @Override
    public void tick() {
        super.tick();
        if(stalker.timeFlying < 20){
            stalker.push(0, 0.1, 0);
        }
    }

    @Override
    public boolean canUse() {
        return stalker.getTarget() != null && !stalker.isFlying() && stalker.getLeapCooldown() == 0;
    }
}
