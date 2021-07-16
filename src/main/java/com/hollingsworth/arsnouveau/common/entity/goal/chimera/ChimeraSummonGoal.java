package com.hollingsworth.arsnouveau.common.entity.goal.chimera;

import com.hollingsworth.arsnouveau.api.event.ChimeraSummonEvent;
import com.hollingsworth.arsnouveau.api.event.EventQueue;
import com.hollingsworth.arsnouveau.common.entity.EntityChimera;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketAnimEntity;
import com.hollingsworth.arsnouveau.common.network.PacketTimedEvent;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;

import java.util.EnumSet;

public class ChimeraSummonGoal extends Goal {
    private EntityChimera mob;
    public int timeSummoning;
    public boolean done;
    public boolean howling;

    public ChimeraSummonGoal(EntityChimera boss){
        this.mob = boss;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public void start() {
        super.start();
        timeSummoning = 0;
        done = false;
        howling = false;
    }


    @Override
    public boolean canContinueToUse() {
        return super.canContinueToUse() && !done;
    }

    @Override
    public boolean canUse() {
        return mob.canSummon();
    }


    @Override
    public void tick() {
        super.tick();
        if(!howling) {
            Networking.sendToNearby(mob.level, mob, new PacketAnimEntity(mob.getId(), EntityChimera.Animations.HOWL.ordinal()));
            ChimeraSummonEvent summonEvent = new ChimeraSummonEvent(60 + mob.getPhase() * 20, mob.getPhase(), mob.level, mob.blockPosition(), this.mob.getId());
            EventQueue.getServerInstance().addEvent(summonEvent);
            Networking.sendToNearby(mob.level, mob, new PacketTimedEvent(summonEvent));
            mob.level.playSound(null, mob.blockPosition(), SoundEvents.WOLF_HOWL, SoundCategory.HOSTILE, 1.0f, 0.2f);
        }
        howling = true;
        timeSummoning++;

        if(timeSummoning >= 80) {
            done = true;
            mob.summonCooldown = 500;
        }
    }
}
