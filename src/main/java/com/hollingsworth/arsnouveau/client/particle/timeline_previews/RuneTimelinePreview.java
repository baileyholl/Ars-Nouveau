package com.hollingsworth.arsnouveau.client.particle.timeline_previews;

import com.hollingsworth.arsnouveau.api.particle.ParticleEmitter;
import com.hollingsworth.arsnouveau.api.particle.PropertyParticleOptions;
import com.hollingsworth.arsnouveau.api.particle.configurations.BurstMotion;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.ColorProperty;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.ParticleTypeProperty;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.PropMap;
import com.hollingsworth.arsnouveau.api.particle.timelines.ParticleTimelinePreview;
import com.hollingsworth.arsnouveau.api.particle.timelines.RuneTimeline;
import com.hollingsworth.arsnouveau.api.particle.timelines.TimelineMap;
import com.hollingsworth.arsnouveau.api.registry.ParticlePropertyRegistry;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.client.registry.ModParticles;
import com.hollingsworth.arsnouveau.common.block.RuneBlock;
import com.hollingsworth.arsnouveau.common.block.tile.RuneTile;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public final class RuneTimelinePreview implements ParticleTimelinePreview {
    private final ParticleEmitter resolveEmitter;
    private final RuneTile rune;
    private boolean active = true;
    private int age;

    public RuneTimelinePreview(RuneTimeline timeline, Level level) {
        PropMap particleProperties = new PropMap();
        particleProperties.set(ParticlePropertyRegistry.COLOR_PROPERTY.get(), new ColorProperty(timeline.getColor(), false));
        PropMap properties = new PropMap();
        properties.set(ParticlePropertyRegistry.TYPE_PROPERTY.get(), new ParticleTypeProperty(ModParticles.NEW_GLOW_TYPE.get(), particleProperties));
        PropertyParticleOptions options = new PropertyParticleOptions(properties);
        resolveEmitter = new ParticleEmitter(() -> new Vec3(0, -0.5, 0), () -> new Vec2(0, 0), new BurstMotion(), options);
        rune = new RuneTile(BlockPos.ZERO, BlockRegistry.RUNE_BLOCK.get().defaultBlockState().setValue(RuneBlock.FACING, Direction.UP));
        rune.spell = new Spell().withTimeline(new TimelineMap().put(timeline.getType(), timeline));
    }

    @Override
    public boolean tick(Level level) {
        int maxTicks = 20;
        if (age < maxTicks) {
            age++;
            return true;
        }
        if (age == maxTicks) {
            active = false;
            resolveEmitter.tick(level);
            age++;
            return true;
        }
        return false;
    }

    @Override
    public float scale() {
        return 30f;
    }

    @Override
    public void renderBlocks(BlockRenderCallback callback) {
        ParticleTimelinePreview.renderGrassField(callback, 1, 1);
        if (active) {
            callback.renderBlockEntity(rune);
        }
    }
}
