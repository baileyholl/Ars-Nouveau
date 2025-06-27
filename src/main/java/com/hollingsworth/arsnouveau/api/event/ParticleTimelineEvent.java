package com.hollingsworth.arsnouveau.api.event;

import com.hollingsworth.arsnouveau.api.particle.ParticleEmitter;
import net.minecraft.world.level.Level;

public class ParticleTimelineEvent implements ITimedEvent {

    public ParticleEmitter emitter;
    public int ticks;
    public Level level;

    public ParticleTimelineEvent(Level level, ParticleEmitter emitter, int ticks) {
        this.level = level;
        this.emitter = emitter;
        this.ticks = ticks;
    }


    @Override
    public void tick(boolean serverSide) {
        ticks--;
        emitter.tick(level);
    }

    @Override
    public boolean isExpired() {
        return ticks <= 0;
    }
}
