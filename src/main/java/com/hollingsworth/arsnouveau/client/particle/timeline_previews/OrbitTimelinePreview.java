package com.hollingsworth.arsnouveau.client.particle.timeline_previews;

import com.hollingsworth.arsnouveau.api.particle.timelines.OrbitTimeline;
import com.hollingsworth.arsnouveau.api.particle.timelines.ParticleTimelinePreview;
import com.hollingsworth.arsnouveau.api.particle.timelines.TimelineMap;
import com.hollingsworth.arsnouveau.api.registry.ParticleTimelineRegistry;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.common.entity.EntityOrbitProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public final class OrbitTimelinePreview implements ParticleTimelinePreview {
    private final List<EntityOrbitProjectile> projectiles = new ArrayList<>();
    private final OrbitTimeline timeline;
    private int age;

    public OrbitTimelinePreview(OrbitTimeline timeline, Level level) {
        this.timeline = timeline;
        Vec3 orbitCenter = new Vec3(0, -1, 0);
        for (int i = 0; i < 3; i++) {
            TimelineMap timelineMap = new TimelineMap().put(ParticleTimelineRegistry.ORBIT_TIMELINE.get(), timeline);
            Spell spell = new Spell().withTimeline(timelineMap);
            EntityOrbitProjectile projectile = new EntityOrbitProjectile(level, orbitCenter.x, orbitCenter.y, orbitCenter.z);
            projectile.setOffset(i);
            projectile.setTotal(3);
            projectile.tracksGround = true;
            projectile.getEntityData().set(EntityOrbitProjectile.LAST_POS, orbitCenter);
            projectile.setResolver(new SpellResolver(new SpellContext(level, spell, null, null)));
            projectiles.add(projectile);
        }
    }

    @Override
    public boolean tick(Level level) {
        if (age > 60) {
            return false;
        }
        if (age == 0) {
            timeline.spawnSound.sound.playSound(level, Vec3.ZERO);
        }
        if (age == 60) {
            timeline.resolveSound.sound.playSound(level, Vec3.ZERO);
        }
        for (EntityOrbitProjectile projectile : projectiles) {
            if (age == 60) {
                projectile.sendResolveParticles();
                continue;
            }
            projectile.setOldPosAndRot();
            projectile.setPos(projectile.getAngledPosition(age));
            projectile.playParticles();
        }
        age++;
        return true;
    }

    @Override
    public void renderEntities(EntityRenderCallback callback) {
        for (EntityOrbitProjectile projectile : projectiles) {
            callback.renderEntity(projectile);
        }
    }

    @Override
    public float scale() {
        return 24f;
    }

    @Override
    public void renderBlocks(BlockRenderCallback callback) {
        ParticleTimelinePreview.renderGrassField(callback, 3, 3);
    }
}
