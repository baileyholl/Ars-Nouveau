package com.hollingsworth.arsnouveau.common.entity.goal.stalker;

import com.hollingsworth.arsnouveau.common.entity.WildenStalker;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketAnimEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class StartFlightGoal extends Goal {
    WildenStalker stalker;

    public StartFlightGoal(WildenStalker stalker) {
        this.stalker = stalker;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public void start() {
        stalker.setLeapCooldown(400);
        stalker.push(0, 0.5, 0);
        stalker.setFlying(true);
        Networking.sendToNearbyClient(stalker.level, stalker, new PacketAnimEntity(stalker.getId(), WildenStalker.Animations.FLY.ordinal()));
    }

    @Override
    public boolean canUse() {
        return stalker.getTarget() != null && !stalker.isFlying() && stalker.getLeapCooldown() == 0;
    }
}
