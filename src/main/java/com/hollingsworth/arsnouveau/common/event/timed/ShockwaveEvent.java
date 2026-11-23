package com.hollingsworth.arsnouveau.common.event.timed;

import com.hollingsworth.arsnouveau.api.event.ITimedEvent;
import com.hollingsworth.arsnouveau.api.particle.ParticleEmitter;
import com.hollingsworth.arsnouveau.api.particle.timelines.EarthshakeTimeline;
import com.hollingsworth.arsnouveau.api.registry.ParticleTimelineRegistry;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.*;

import java.util.HashSet;
import java.util.Set;

public class ShockwaveEvent implements ITimedEvent {

    private final ServerLevel level;
    private final Vec3 origin;
    private final BlockPos center;
    private final Direction face;
    private final double maxRadius;
    private final boolean targetsBlocks;
    private final int pierce;
    private final SpellResolver resolver;
    private final ParticleEmitter particleEmitter;
    private final Set<Integer> resolvedEntities = new HashSet<>();
    private final Set<BlockPos> resolvedBlocks = new HashSet<>();
    private double radius;
    private boolean expired;

    public ShockwaveEvent(ServerLevel level, Vec3 origin, BlockPos center, Direction face, double maxRadius, boolean targetsBlocks, int pierce, SpellResolver resolver) {
        this.level = level;
        this.origin = origin;
        this.center = center;
        this.face = face;
        this.maxRadius = maxRadius;
        this.targetsBlocks = targetsBlocks;
        this.pierce = pierce;
        this.resolver = resolver;
        EarthshakeTimeline timeline = resolver.spellContext.getParticleTimeline(ParticleTimelineRegistry.EARTHSHAKE_TIMELINE.get());
        this.particleEmitter = new ParticleEmitter(() -> origin, () -> new Vec2(0, 0), timeline.waveEffect);
    }

    @Override
    public void tick(boolean serverSide) {
        if (!serverSide || !resolver.spellContext.getUnwrappedCaster().isAlive()) {
            expired = true;
            return;
        }

        double previousRadius = radius;
        radius = Math.min(radius + 1.0, maxRadius);
        particleEmitter.tick(level);
        if (targetsBlocks) {
            resolveBlocks(previousRadius);
        } else {
            resolveEntities(previousRadius);
        }
        if (radius >= maxRadius) {
            expired = true;
        }
    }

    private void resolveEntities(double previousRadius) {
        Entity caster = resolver.spellContext.getUnwrappedCaster();
        Direction.Axis axis = face.getAxis();
        AABB bounds = new AABB(origin.relative(face, 0.7), origin.relative(face, -pierce))
                .inflate(axis == Direction.Axis.X ? 0 : maxRadius, axis == Direction.Axis.Y ? 0 : maxRadius, axis == Direction.Axis.Z ? 0 : maxRadius);
        for (Entity entity : level.getEntities(caster, bounds)) {
            if (resolvedEntities.contains(entity.getId())) {
                continue;
            }
            double distance = entity.position().subtract(origin).with(axis, 0).length();
            double entityRadius = entity.getBbWidth() * 0.5;
            if (distance + entityRadius >= previousRadius && distance - entityRadius <= radius) {
                resolver.getNewResolver(resolver.spellContext.clone()).onResolveEffect(level, new EntityHitResult(entity));
                resolvedEntities.add(entity.getId());
            }
        }
    }

    private void resolveBlocks(double previousRadius) {
        int bound = (int) Math.ceil(radius);
        Direction.Axis axis = face.getAxis();
        BlockPos extent = new BlockPos(axis == Direction.Axis.X ? 0 : bound, axis == Direction.Axis.Y ? 0 : bound, axis == Direction.Axis.Z ? 0 : bound);
        for (BlockPos pos : BlockPos.betweenClosed(center.subtract(extent).relative(face.getOpposite(), pierce), center.offset(extent))) {
            BlockPos immutablePos = pos.immutable();
            if (resolvedBlocks.contains(immutablePos)) {
                continue;
            }
            double distance = pos.getCenter().subtract(origin).with(axis, 0).length();
            if (distance >= previousRadius && distance <= radius) {
                resolver.getNewResolver(resolver.spellContext.clone()).onResolveEffect(level,
                        new BlockHitResult(pos.getCenter(), face, immutablePos, axis.isVertical()));
                resolvedBlocks.add(immutablePos);
            }
        }
    }

    @Override
    public boolean isExpired() {
        return expired;
    }
}
