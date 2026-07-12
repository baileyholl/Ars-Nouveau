package com.hollingsworth.arsnouveau.api.particle.timelines;

import com.hollingsworth.arsnouveau.api.particle.ParticleEmitter;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.util.function.Supplier;

public final class ProjectileTimelinePreview implements ParticleTimelinePreview {
    private final int flightTicks = 40;

    private final ParticleEmitter spawnEmitter;
    private final ParticleEmitter trailEmitter;
    private final ParticleEmitter flairEmitter;
    private final ParticleEmitter resolveEmitter;
    private final Vec3 velocity;
    private Vec3 position;
    private int age;

    public ProjectileTimelinePreview(ProjectileTimeline timeline, Vec3 origin) {
        this.position = origin.add(-3.5, 0, 0);
        this.velocity = origin.add(1.5, 0, 0).subtract(position).scale(1f / flightTicks);
        Supplier<Vec3> positionGetter = () -> position;
        Supplier<Vec2> rotationGetter = () -> new Vec2(0, -90);
        spawnEmitter = new ParticleEmitter(positionGetter, rotationGetter, timeline.onSpawnEffect);
        trailEmitter = new ParticleEmitter(positionGetter, rotationGetter, timeline.trailEffect);
        flairEmitter = new ParticleEmitter(positionGetter, rotationGetter, timeline.flairEffect);
        resolveEmitter = new ParticleEmitter(positionGetter, rotationGetter, timeline.onResolvingEffect);
    }

    @Override
    public boolean tick(Level level) {
        if (age > flightTicks) {
            return false;
        }
        if (age == flightTicks) {
            resolveEmitter.tick(level);
        } else {
            if (age == 0) {
                spawnEmitter.tick(level);
            }
            position = position.add(velocity);
            trailEmitter.tick(level);
            flairEmitter.tick(level);
        }
        age++;
        return true;
    }
}
