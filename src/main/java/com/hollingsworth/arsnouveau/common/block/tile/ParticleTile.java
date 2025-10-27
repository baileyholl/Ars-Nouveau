package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.particle.ParticleEmitter;
import com.hollingsworth.arsnouveau.api.particle.timelines.PrestidigitationTimeline;
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

public class ParticleTile extends ModdedTile implements ITickable {
    protected PrestidigitationTimeline timeline = new PrestidigitationTimeline();
    public ParticleEmitter particleEmitter;
    public boolean isTemporary;
    public int ticksRemaining;

    public ParticleTile(BlockPos pos, BlockState state) {
        this(BlockRegistry.PARTICLE_TILE.get(), pos, state);
    }

    public ParticleTile(BlockEntityType<?> lightTile, BlockPos pos, BlockState state) {
        super(lightTile, pos, state);
    }

    @Override
    public void tick(Level level, BlockState state, BlockPos pos) {
        if (!level.isClientSide && isTemporary) {
            if (ticksRemaining <= 0) {
                level.removeBlock(pos, false);
                return;
            }
            ticksRemaining--;
        }
        if (level.isClientSide) {
            if (particleEmitter != null) {
                particleEmitter.tick(level);
            }
        }
    }

    public void playSound() {
        if (this.timeline.randomSound != null && this.timeline.randomSound.sound != null) {
            this.timeline.randomSound.sound.playSound(level, this.getX(), this.getY(), this.getZ());
        }
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag compound, HolderLookup.@NotNull Provider pRegistries) {
        super.loadAdditional(compound, pRegistries);
        this.timeline = ANCodecs.decode(PrestidigitationTimeline.CODEC.codec(), compound.getCompound("timeline"));
        particleEmitter = new ParticleEmitter(() -> this.getBlockPos().getCenter(), () -> new Vec2(0, 0), timeline.onTickEffect);
        this.isTemporary = compound.getBoolean("isTemporary");
        this.ticksRemaining = compound.getInt("ticksRemaining");
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider pRegistries) {
        super.saveAdditional(tag, pRegistries);
        tag.put("timeline", ANCodecs.encode(PrestidigitationTimeline.CODEC.codec(), timeline));
        tag.putBoolean("isTemporary", isTemporary);
        tag.putInt("ticksRemaining", ticksRemaining);
    }

    public void setTimeline(PrestidigitationTimeline timeline) {
        this.timeline = timeline;
        this.particleEmitter = new ParticleEmitter(() -> this.getBlockPos().getCenter(), () -> new Vec2(0, 0), timeline.onTickEffect);
        updateBlock();
    }
}
