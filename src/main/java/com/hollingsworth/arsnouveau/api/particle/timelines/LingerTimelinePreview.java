package com.hollingsworth.arsnouveau.api.particle.timelines;

import com.hollingsworth.arsnouveau.api.registry.ParticleTimelineRegistry;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.common.entity.EntityLingeringSpell;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class LingerTimelinePreview implements ParticleTimelinePreview {
    private EntityLingeringSpell linger;
    private int age;

    public LingerTimelinePreview(LingerTimeline timeline, Level level, Vec3 origin) {
        Vec3 position = origin.add(0, -1, 0);
        TimelineMap timelineMap = new TimelineMap().put(ParticleTimelineRegistry.LINGER_TIMELINE.get(), timeline);
        Spell spell = new Spell().withTimeline(timelineMap);
        linger = new EntityLingeringSpell(level, position.x, position.y, position.z);
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
    public void renderWorldBlocks(BlockRenderCallback callback) {
        ParticleTimelinePreview.renderGrassField(callback, 6, 6);
    }
}
