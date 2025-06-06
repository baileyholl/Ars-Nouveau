package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.particle.ParticleEmitter;
import com.hollingsworth.arsnouveau.api.particle.PropertyParticleOptions;
import com.hollingsworth.arsnouveau.api.particle.configurations.LightBlobMotion;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.ColorProperty;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.PropMap;
import com.hollingsworth.arsnouveau.api.particle.timelines.LightTimeline;
import com.hollingsworth.arsnouveau.api.particle.timelines.TimelineEntryData;
import com.hollingsworth.arsnouveau.api.registry.ParticleColorRegistry;
import com.hollingsworth.arsnouveau.api.registry.ParticlePropertyRegistry;
import com.hollingsworth.arsnouveau.api.util.IWololoable;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.common.util.ANCodecs;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
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
        if (level.isClientSide) {
            if(particleEmitter != null){
                particleEmitter.tick(level);
            }
        }
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag compound, HolderLookup.@NotNull Provider pRegistries) {
        super.loadAdditional(compound, pRegistries);
        this.color = ParticleColorRegistry.from(compound.getCompound("color"));
        if(compound.contains("timeline")) {
            this.timeline = ANCodecs.decode(LightTimeline.CODEC.codec(), compound.getCompound("timeline"));
        }else{
            this.timeline = new LightTimeline();
            PropertyParticleOptions particleOptions = new PropertyParticleOptions(new PropMap());
            ColorProperty colorProperty = new ColorProperty(new PropMap());
            colorProperty.particleColor = color;
            particleOptions.map.set(ParticlePropertyRegistry.COLOR_PROPERTY.get(), colorProperty);
            timeline.onTickEffect = new TimelineEntryData(new LightBlobMotion(), particleOptions);
        }
        particleEmitter = new ParticleEmitter(() -> this.getBlockPos().getCenter(), () -> new Vec2(0, 0), timeline.onTickEffect);
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider pRegistries) {
        super.saveAdditional(tag, pRegistries);
        tag.put("color", color.serialize());
        tag.put("timeline", ANCodecs.encode(LightTimeline.CODEC.codec(), timeline));
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
