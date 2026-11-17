package com.hollingsworth.arsnouveau.client.particle.timeline_previews;

import com.hollingsworth.arsnouveau.api.particle.timelines.LingerTimeline;
import com.hollingsworth.arsnouveau.api.particle.timelines.ParticleTimelinePreview;
import com.hollingsworth.arsnouveau.api.particle.timelines.TimelineMap;
import com.hollingsworth.arsnouveau.api.registry.ParticleTimelineRegistry;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.common.entity.EntityLingeringSpell;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class LingerTimelinePreview implements ParticleTimelinePreview {
    private final LingerTimeline timeline;
    private EntityLingeringSpell linger;
    private int age;

    public LingerTimelinePreview(LingerTimeline timeline, Level level) {
        this.timeline = timeline;
        TimelineMap timelineMap = new TimelineMap().put(ParticleTimelineRegistry.LINGER_TIMELINE.get(), timeline);
        Spell spell = new Spell().withTimeline(timelineMap);
        linger = new EntityLingeringSpell(level, 0, 0, 0);
        linger.setShouldFall(false);
        linger.setLanded(true);
        linger.setResolver(new SpellResolver(new SpellContext(level, spell, null, null)));
    }

    @Override
    public boolean tick(Level level) {
        if (age >= 40) {
            return false;
        }
        if (linger != null) {
            linger.playParticles();
        }
        age++;
        if (age == 30) {
            linger.sendResolveParticles();
            timeline.resolveSound.sound.playSound(level, Vec3.ZERO);
            linger = null;
        }
        return true;
    }

    @Override
    public void renderEntities(EntityRenderCallback callback) {
        if (linger != null) {
            callback.renderEntity(linger);
        }
    }

    @Override
    public float scale() {
        return 12f;
    }

    @Override
    public void renderBlocks(BlockRenderCallback callback) {
        ParticleTimelinePreview.renderGrassField(callback, 6, 6);
    }
}
