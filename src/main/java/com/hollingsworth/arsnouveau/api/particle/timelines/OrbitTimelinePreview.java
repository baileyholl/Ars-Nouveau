package com.hollingsworth.arsnouveau.api.particle.timelines;

import com.hollingsworth.arsnouveau.api.particle.ParticleEmitter;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.util.function.Supplier;

public final class OrbitTimelinePreview implements ParticleTimelinePreview {
    private final Vec3[] positions = new Vec3[3];
    private final ParticleEmitter[] spawnEmitters = new ParticleEmitter[positions.length];
    private final ParticleEmitter[] trailEmitters = new ParticleEmitter[positions.length];
    private final ParticleEmitter[] flairEmitters = new ParticleEmitter[positions.length];
    private final ParticleEmitter[] resolveEmitters = new ParticleEmitter[positions.length];
    private final Vec3 origin;
    private int age;

    public OrbitTimelinePreview(OrbitTimeline timeline, Vec3 origin) {
        this.origin = origin.add(0, -0.5, 0);
        for (int i = 0; i < positions.length; i++) {
            int orb = i;
            positions[i] = getPosition(i, 0);
            Supplier<Vec3> positionGetter = () -> positions[orb];
            Supplier<Vec2> rotationGetter = () -> new Vec2(0, 0);
            spawnEmitters[i] = new ParticleEmitter(positionGetter, rotationGetter, timeline.onSpawnEffect);
            trailEmitters[i] = new ParticleEmitter(positionGetter, rotationGetter, timeline.trailEffect);
            flairEmitters[i] = new ParticleEmitter(positionGetter, rotationGetter, timeline.flairEffect);
            resolveEmitters[i] = new ParticleEmitter(positionGetter, rotationGetter, timeline.onResolvingEffect);
        }
    }

    @Override
    public boolean tick(Level level) {
        if (age > 60) {
            return false;
        }
        if (age == 60) {
            for (ParticleEmitter emitter : resolveEmitters) {
                emitter.tick(level);
            }
        } else {
            for (int i = 0; i < positions.length; i++) {
                positions[i] = getPosition(i, age);
                if (age == 0) {
                    spawnEmitters[i].tick(level);
                }
                trailEmitters[i].tick(level);
                flairEmitters[i].tick(level);
            }
        }
        age++;
        return true;
    }

    private Vec3 getPosition(int orb, int tick) {
        double angle = tick / 10.0 + orb * 15;
        return origin.add(-1.5 * Math.sin(angle), 0, -1.5 * Math.cos(angle));
    }

    @Override
    public float scale() {
        return 24f;
    }

    @Override
    public void renderWorldBlocks(BlockRenderCallback callback) {
        ParticleTimelinePreview.renderGrassField(callback, 2, 2);
    }
}
