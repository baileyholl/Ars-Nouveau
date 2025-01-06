package com.hollingsworth.arsnouveau.common.entity.goal.chimera;

import com.hollingsworth.arsnouveau.api.event.ChimeraSummonEvent;
import com.hollingsworth.arsnouveau.api.event.EventQueue;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.entity.WildenChimera;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketTimedEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class ChimeraSummonGoal extends Goal {
    private WildenChimera mob;
    public int timeSummoning;
    public boolean done;
    public boolean howling;

    public ChimeraSummonGoal(WildenChimera boss) {
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
    public boolean isInterruptable() {
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return !done && !mob.getPhaseSwapping();
    }

    @Override
    public boolean canUse() {
        return mob.canSummon() && !mob.isSwimming();
    }


    @Override
    public void tick() {
        super.tick();

        if (!howling) {
            howling = true;
            this.mob.setHowling(true);
            ChimeraSummonEvent summonEvent = new ChimeraSummonEvent(40 + mob.getPhase() * 20, mob.getPhase(), mob.level, mob.blockPosition(), this.mob.getId());
            EventQueue.getServerInstance().addEvent(summonEvent);
            Networking.sendToNearbyClient(mob.level, mob, new PacketTimedEvent(summonEvent));
            mob.level.playSound(null, mob.blockPosition(), SoundEvents.WOLF_HOWL, SoundSource.HOSTILE, 1.0f, 0.2f);
        }
        timeSummoning++;
        if (timeSummoning >= 80) {
            done = true;
            mob.summonCooldown = (int) (1000 + ParticleUtil.inRange(-100, 100) + mob.getCooldownModifier());
            mob.setHowling(false);
        }
    }
}
