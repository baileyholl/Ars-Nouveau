package com.hollingsworth.arsnouveau.api.event;

import com.hollingsworth.arsnouveau.api.particle.ParticleEmitter;
import com.hollingsworth.arsnouveau.api.particle.timelines.DelayTimeline;
import com.hollingsworth.arsnouveau.api.registry.ParticleTimelineRegistry;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec2;

public class DelayedSpellEvent implements ITimedEvent {
    public int duration;
    public final SpellResolver resolver;
    public final HitResult result;
    public final Level world;
    public final boolean showParticles;
    public ParticleEmitter emitter;

    public DelayedSpellEvent(int delay, HitResult result, Level world, SpellResolver resolver) {
        this(delay, result, world, resolver, true);
    }

    public DelayedSpellEvent(int delay, HitResult result, Level world, SpellResolver resolver, boolean showParticles) {
        this.duration = delay;
        this.result = result;
        this.world = world;
        this.resolver = resolver;
        this.showParticles = showParticles;
    }

    @Override
    public void tick(boolean serverSide) {
        duration--;
        if (duration <= 0 && serverSide) {
            resolveSpell();
        } else if (!serverSide && result != null) {
            if (emitter == null) {
                DelayTimeline delayTimeline = resolver.spell.particleTimeline().get(ParticleTimelineRegistry.DELAY_TIMELINE.get());
                emitter = new ParticleEmitter(result::getLocation, () -> new Vec2(0, 0), delayTimeline.onTickEffect);
            }
            if (showParticles) {
                emitter.tick(world);
                if (duration <= 0) {
                    DelayTimeline delayTimeline = resolver.spell.particleTimeline().get(ParticleTimelineRegistry.DELAY_TIMELINE.get());
                    ParticleEmitter resolveEmitter = new ParticleEmitter(result::getLocation, () -> new Vec2(0, 0), delayTimeline.onResolvingEffect);
                    resolveEmitter.tick(world);
                }
            }
        }
    }

    public void resolveSpell() {
        if (world == null)
            return;
        if (result instanceof EntityHitResult ehr && ehr.getEntity().isRemoved()) {
            return;
        }
        DelayTimeline delayTimeline = resolver.spell.particleTimeline().get(ParticleTimelineRegistry.DELAY_TIMELINE.get());
        delayTimeline.resolvingSound().sound.playSound(world, result.getLocation());
        resolver.resume(world);
    }

    @Override
    public boolean isExpired() {
        return duration <= 0 || world == null;
    }
}
