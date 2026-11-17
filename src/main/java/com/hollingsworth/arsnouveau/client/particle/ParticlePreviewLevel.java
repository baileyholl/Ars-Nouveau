package com.hollingsworth.arsnouveau.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class ParticlePreviewLevel extends ClientLevel {

    @FunctionalInterface
    public interface IParticleAdded {
        void particleAdded(ParticleOptions options, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed);
    }

    public final Map<BlockPos, BlockEntity> blockEntityMap = new HashMap<>();
    private final Map<BlockPos, BlockState> positions = new HashMap<>();
    private final IParticleAdded particleAddedCallback;

    public ParticlePreviewLevel(ClientLevel realLevel, IParticleAdded particleAddedCallback) {
        super(Minecraft.getInstance().getConnection(), new ClientLevelData(Difficulty.NORMAL, false, true),
                realLevel.dimension(), realLevel.dimensionTypeRegistration(), 3, 3,
                Minecraft.getInstance()::getProfiler, Minecraft.getInstance().levelRenderer, false, 0);
        this.particleAddedCallback = particleAddedCallback;
    }

    @Override
    public BlockState getBlockState(BlockPos pos) {
        return positions.getOrDefault(pos, Blocks.AIR.defaultBlockState());
    }

    @Override
    public boolean setBlock(BlockPos pos, BlockState state, int flags, int recursionLeft) {
        positions.put(pos.immutable(), state);
        return true;
    }

    @Override
    public FluidState getFluidState(BlockPos pos) {
        BlockState state = positions.get(pos);
        return state == null ? Fluids.EMPTY.defaultFluidState() : state.getFluidState();
    }

    @Nullable
    @Override
    public BlockEntity getBlockEntity(BlockPos pos) {
        return blockEntityMap.get(pos);
    }

    @Override
    public boolean hasChunkAt(int x, int z) {
        return true;
    }

    @Override
    public int getBrightness(LightLayer lightType, BlockPos pos) {
        return 15;
    }

    @Override
    public int getRawBrightness(BlockPos pos, int amount) {
        return 15;
    }

    @Override
    public void playSound(@Nullable Player player, double x, double y, double z, SoundEvent sound, SoundSource source, float volume, float pitch) {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(sound, pitch, volume));
    }

    @Override
    public void playSeededSound(@Nullable Player player, double x, double y, double z, Holder<SoundEvent> sound, SoundSource source, float volume, float pitch, long seed) {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(sound.value(), pitch, volume));
    }

    @Override
    public void playSeededSound(@Nullable Player player, Entity entity, Holder<SoundEvent> sound, SoundSource source, float volume, float pitch, long seed) {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(sound.value(), pitch, volume));
    }

    @Override
    public void addParticle(ParticleOptions options, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        particleAddedCallback.particleAdded(options, x, y, z, xSpeed, ySpeed, zSpeed);
    }

    @Override
    public void addParticle(ParticleOptions options, boolean forceAlwaysRender, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        addParticle(options, x, y, z, xSpeed, ySpeed, zSpeed);
    }

    @Override
    public void addAlwaysVisibleParticle(ParticleOptions options, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        addParticle(options, x, y, z, xSpeed, ySpeed, zSpeed);
    }

    @Override
    public void addAlwaysVisibleParticle(ParticleOptions options, boolean ignoreRange, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        addParticle(options, x, y, z, xSpeed, ySpeed, zSpeed);
    }

    @Override
    public void sendBlockUpdated(BlockPos pos, BlockState oldState, BlockState newState, int flags) {
    }

    @Override
    public void levelEvent(@Nullable Player player, int type, BlockPos pos, int data) {
    }
}
