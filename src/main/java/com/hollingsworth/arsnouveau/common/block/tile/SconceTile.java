package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.entity.IDispellable;
import com.hollingsworth.arsnouveau.api.particle.ParticleEmitter;
import com.hollingsworth.arsnouveau.api.particle.PropertyParticleOptions;
import com.hollingsworth.arsnouveau.api.particle.configurations.LightBlobMotion;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.ColorProperty;
import com.hollingsworth.arsnouveau.api.particle.timelines.LightTimeline;
import com.hollingsworth.arsnouveau.api.particle.timelines.TimelineEntryData;
import com.hollingsworth.arsnouveau.api.registry.ParticleColorRegistry;
import com.hollingsworth.arsnouveau.api.registry.ParticlePropertyRegistry;
import com.hollingsworth.arsnouveau.api.registry.ParticleTimelineRegistry;
import com.hollingsworth.arsnouveau.api.spell.ILightable;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.hollingsworth.arsnouveau.api.util.IWololoable;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.client.registry.ModParticles;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.common.block.SconceBlock;
import com.hollingsworth.arsnouveau.common.block.ScribesBlock;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDampen;
import com.hollingsworth.arsnouveau.common.util.ANCodecs;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class SconceTile extends ModdedTile implements ILightable, ITickable, IDispellable, IWololoable {
    protected LightTimeline timeline = new LightTimeline();
    protected ParticleColor color = ParticleColor.defaultParticleColor();
    public boolean lit;
    ParticleEmitter particleEmitter;

    public SconceTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.SCONCE_TILE, pos, state);
    }

    public SconceTile(BlockEntityType type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void loadAdditional(CompoundTag compound, HolderLookup.Provider pRegistries) {
        super.loadAdditional(compound, pRegistries);
        this.color = ParticleColorRegistry.from(compound.getCompound("color"));
        lit = compound.getBoolean("lit");
        if(compound.contains("timeline")) {
            this.timeline = ANCodecs.decode(LightTimeline.CODEC.codec(), compound.getCompound("timeline"));
        }else{
            this.timeline = new LightTimeline();
            PropertyParticleOptions particleOptions = new PropertyParticleOptions(ModParticles.NEW_GLOW_TYPE.get());
            ColorProperty colorProperty = new ColorProperty();
            colorProperty.particleColor = color;
            particleOptions.map.set(ParticlePropertyRegistry.COLOR_PROPERTY.get(), colorProperty);
            timeline.onTickEffect = new TimelineEntryData(new LightBlobMotion(), particleOptions);
        }
        particleEmitter = new ParticleEmitter(() -> this.getBlockPos().getCenter(), () -> new Vec2(0, 0), timeline.onTickEffect);
    }

    @Override
    public void saveAdditional(CompoundTag compound, HolderLookup.Provider pRegistries) {
        super.saveAdditional(compound, pRegistries);
        compound.put("color", color.serialize());
        compound.putBoolean("lit", lit);
        compound.put("timeline", ANCodecs.encode(LightTimeline.CODEC.codec(), timeline));
    }

    @Override
    public void onLight(HitResult rayTraceResult, Level world, LivingEntity shooter, SpellStats stats, SpellContext spellContext) {
        this.color = spellContext.getParticleTimeline(ParticleTimelineRegistry.LIGHT_TIMELINE.get()).onTickEffect.particleOptions().colorProp().particleColor;
        this.timeline = spellContext.getParticleTimeline(ParticleTimelineRegistry.LIGHT_TIMELINE.get());
        particleEmitter = new ParticleEmitter(() -> this.getBlockPos().getCenter(), () -> new Vec2(0, 0), timeline.onTickEffect);
        lit = true;
        if (rayTraceResult instanceof BlockHitResult) {
            BlockState state = world.getBlockState(((BlockHitResult) rayTraceResult).getBlockPos());
            world.setBlock(getBlockPos(), state.setValue(SconceBlock.LIGHT_LEVEL, Math.min(Math.max(0, 15 - stats.getBuffCount(AugmentDampen.INSTANCE)), 15)), 3);
            world.sendBlockUpdated(((BlockHitResult) rayTraceResult).getBlockPos(), state,
                    state.setValue(SconceBlock.LIGHT_LEVEL, Math.min(Math.max(0, 15 - stats.getBuffCount(AugmentDampen.INSTANCE)), 15)), 3);
        }
        updateBlock();
    }

    @Override
    public void tick() {
        if (!level.isClientSide() || !lit)
            return;
        BlockPos pos = getBlockPos();
        double xzOffset = 0.15;
        BlockState state = getLevel().getBlockState(getBlockPos());
        if (!(state.hasProperty(ScribesBlock.FACING)))
            return;

        double xOffset = ParticleUtil.inRange(-xzOffset / 4, xzOffset / 4);
        double zOffset = ParticleUtil.inRange(-xzOffset / 4, xzOffset / 4);
        double centerX = pos.getX() + xOffset;
        double centerZ = pos.getZ() + zOffset;
        if (state.getValue(ScribesBlock.FACING) == Direction.NORTH) {
            centerX += 0.5;
            centerZ += 0.8;
        }
        if (state.getValue(ScribesBlock.FACING) == Direction.SOUTH) {
            centerX += 0.5;
            centerZ += 0.2;
        }
        if (state.getValue(ScribesBlock.FACING) == Direction.EAST) {
            centerX += 0.2;
            centerZ += 0.5;
        }
        if (state.getValue(ScribesBlock.FACING) == Direction.WEST) {
            centerX += 0.8;
            centerZ += 0.5;
        }
        if(particleEmitter != null) {
            particleEmitter.setPosition(new Vec3(centerX, pos.getY() + 0.9 + ParticleUtil.inRange(-0.00, 0.1), centerZ));
            particleEmitter.tick(level);
        }
    }

    @Override
    public boolean onDispel(@Nullable LivingEntity caster) {
        this.lit = false;
        level.setBlock(getBlockPos(), level.getBlockState(getBlockPos()).setValue(SconceBlock.LIGHT_LEVEL, 0), 3);
        updateBlock();
        return true;
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