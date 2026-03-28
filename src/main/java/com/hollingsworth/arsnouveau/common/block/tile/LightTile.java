package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.particle.ParticleEmitter;
import com.hollingsworth.arsnouveau.api.particle.PropertyParticleOptions;
import com.hollingsworth.arsnouveau.api.particle.configurations.LightBlobMotion;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.ColorProperty;
import com.hollingsworth.arsnouveau.api.particle.timelines.LightTimeline;
import com.hollingsworth.arsnouveau.api.particle.timelines.TimelineEntryData;
import com.hollingsworth.arsnouveau.api.registry.ParticlePropertyRegistry;
import com.hollingsworth.arsnouveau.api.util.IWololoable;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.registry.ModParticles;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.NotNull;

public class LightTile extends ModdedTile implements ITickable, IWololoable {
    protected LightTimeline timeline = new LightTimeline();
    public ParticleColor color = ParticleColor.defaultParticleColor();
    public ParticleEmitter particleEmitter;

    public LightTile(BlockPos pos, BlockState state) {
        this(BlockRegistry.LIGHT_TILE.get(), pos, state);
    }

    public LightTile(BlockEntityType<?> lightTile, BlockPos pos, BlockState state) {
        super(lightTile, pos, state);
    }

    @Override
    public void tick(Level level, BlockState state, BlockPos pos) {
        if (level.isClientSide()) {
            if (particleEmitter != null) {
                particleEmitter.tick(level);
            }
        }
    }

    @Override
    protected void loadAdditional(@NotNull ValueInput compound) {
        super.loadAdditional(compound);
        this.color = compound.read("color", ParticleColor.CODEC.codec()).orElseGet(ParticleColor::defaultParticleColor);
        this.timeline = compound.read("timeline", LightTimeline.CODEC.codec()).orElseGet(() -> {
            LightTimeline newTimeline = new LightTimeline();
            PropertyParticleOptions particleOptions = new PropertyParticleOptions(ModParticles.NEW_GLOW_TYPE.get());
            ColorProperty colorProperty = new ColorProperty();
            colorProperty.particleColor = color;
            particleOptions.map.set(ParticlePropertyRegistry.COLOR_PROPERTY.get(), colorProperty);
            newTimeline.onTickEffect = new TimelineEntryData(new LightBlobMotion(), particleOptions);
            return newTimeline;
        });
        particleEmitter = new ParticleEmitter(() -> this.getBlockPos().getCenter(), () -> new Vec2(0, 0), timeline.onTickEffect);
    }

    @Override
    public void saveAdditional(@NotNull ValueOutput tag) {
        super.saveAdditional(tag);
        tag.store("color", ParticleColor.CODEC.codec(), color);
        tag.store("timeline", LightTimeline.CODEC.codec(), timeline);
    }

    public void setTimeline(LightTimeline timeline) {
        this.timeline = timeline;
        this.particleEmitter = new ParticleEmitter(() -> this.getBlockPos().getCenter(), () -> new Vec2(0, 0), timeline.onTickEffect);
        updateBlock();
    }

    @Override
    public void setColor(ParticleColor color) {
        this.color = color;
        updateBlock();
    }

    @Override
    public ParticleColor getColor() {
        return color;
    }
}
