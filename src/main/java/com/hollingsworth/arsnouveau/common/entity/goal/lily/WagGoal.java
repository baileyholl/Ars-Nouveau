package com.hollingsworth.arsnouveau.common.entity.goal.lily;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

import java.util.EnumSet;

public class WagGoal<T extends Mob & IAdorable> extends Goal {
    T lily;
    public LivingEntity target;
    public int wagAtTicks;

    public WagGoal(T lily) {
        this.lily = lily;
        setFlags(EnumSet.of(Goal.Flag.LOOK));
    }

    @Override
    public void tick() {
        super.tick();
        if(wagAtTicks > 0){
            wagAtTicks--;
        }
        if(target != null){
            lily.getLookControl().setLookAt(target, 30, 30);
        }

    }

    @Override
    public boolean canUse() {
        target = null;
        ServerLevel level = (ServerLevel) lily.level;
        for(Player player : level.getPlayers(p -> true)) {
            if (player.distanceTo(lily) < 5 && lily.isLookingAtMe(player)) {
                target = player;
                wagAtTicks = 100;
                lily.setWagging(true);
                lily.setWagTicks(100);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return wagAtTicks > 0;
    }
}
