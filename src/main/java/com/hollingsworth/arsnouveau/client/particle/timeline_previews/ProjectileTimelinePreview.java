package com.hollingsworth.arsnouveau.client.particle.timeline_previews;

import com.hollingsworth.arsnouveau.api.particle.timelines.ParticleTimelinePreview;
import com.hollingsworth.arsnouveau.api.particle.timelines.ProjectileTimeline;
import com.hollingsworth.arsnouveau.api.particle.timelines.TimelineMap;
import com.hollingsworth.arsnouveau.api.registry.ParticleTimelineRegistry;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.common.entity.EntityProjectileSpell;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class ProjectileTimelinePreview implements ParticleTimelinePreview {
    private final int flightTicks = 40;

    private final Vec3 velocity;
    private final ProjectileTimeline timeline;
    private EntityProjectileSpell projectile;
    private int age;

    public ProjectileTimelinePreview(ProjectileTimeline timeline, Level level) {
        Vec3 position = new Vec3(-3.5, 0, 1);
        this.velocity = new Vec3(1.5, 0, 1).subtract(position).scale(1f / flightTicks);
        this.timeline = timeline;
        TimelineMap timelineMap = new TimelineMap().put(ParticleTimelineRegistry.PROJECTILE_TIMELINE.get(), timeline);
        Spell spell = new Spell().withTimeline(timelineMap);
        projectile = new EntityProjectileSpell(level, position.x, position.y, position.z);
        projectile.setResolver(new SpellResolver(new SpellContext(level, spell, null, null)));
        projectile.shoot(velocity.x, velocity.y, velocity.z, (float) velocity.length(), 0);
        projectile.setOldPosAndRot();
    }

    @Override
    public boolean tick(Level level) {
        if (age > flightTicks) {
            return false;
        }
        if (age == 0) {
            timeline.castSound.sound.playSound(level, Vec3.ZERO);
        }
        projectile.setOldPosAndRot();
        if (age == flightTicks) {
            projectile.sendResolveParticles();
            timeline.resolveSound.sound.playSound(level, Vec3.ZERO);
            projectile = null;
        } else {
            projectile.setPos(projectile.getPosition(0).add(velocity));
            projectile.playParticles();
        }
        age++;
        return true;
    }

    @Override
    public float scale() {
        return 26f;
    }

    @Override
    public void renderEntities(EntityRenderCallback callback) {
        if (projectile != null) {
            callback.renderEntity(projectile);
        }
    }

}
