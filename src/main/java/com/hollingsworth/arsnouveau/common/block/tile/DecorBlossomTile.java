package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.item.IWandable;
import com.hollingsworth.arsnouveau.api.particle.ParticleEmitter;
import com.hollingsworth.arsnouveau.api.particle.timelines.PrestidigitationTimeline;
import com.hollingsworth.arsnouveau.api.registry.ParticleTimelineRegistry;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectDispel;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectPrestidigitation;
import com.hollingsworth.arsnouveau.common.util.ANCodecs;
import com.hollingsworth.arsnouveau.setup.config.ServerConfig;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class DecorBlossomTile extends ModdedTile implements ITickable, IWandable, IResolveListener, GeoBlockEntity {
    protected PrestidigitationTimeline timeline;
    protected ParticleEmitter emitter;
    protected BlockPos emitPos;

    public DecorBlossomTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.DECOR_BLOSSOM_TILE.get(), pos, state);
    }

    @Override
    public void tick() {
        if (timeline != null && level.isClientSide && emitPos != null) {
            if (emitter == null) {
                emitter = new ParticleEmitter(() -> this.emitPos.getCenter(), () -> new Vec2(0, 0), timeline.onTickEffect);
            }

            emitter.tick(level);
        }
    }

    public void onRandomTick() {
        if (this.timeline != null && this.timeline.randomSound != null && this.timeline.randomSound.sound != null && emitPos != null) {
            this.timeline.randomSound.sound.playSound(level, emitPos.getCenter());
        }
    }


    @Override
    public Result onFirstConnection(@Nullable GlobalPos storedPos, @Nullable Direction face, @Nullable LivingEntity storedEntity, Player playerEntity) {
        if (storedPos == null) {
            return IWandable.super.onFirstConnection(storedPos, face, storedEntity, playerEntity);
        }
        if (BlockUtil.distanceFrom(storedPos.pos(), this.worldPosition) > ServerConfig.DECOR_BLOSSOM_RANGE.get()) {
            playerEntity.sendSystemMessage(Component.translatable("ars_nouveau.connection.range", ServerConfig.DECOR_BLOSSOM_RANGE.get()));
            return Result.FAIL;
        }
        if (storedPos != null) {
            this.emitPos = storedPos.pos().above();
            updateBlock();
            return Result.SUCCESS;
        }
        return IWandable.super.onFirstConnection(storedPos, face, storedEntity, playerEntity);
    }

    @Override
    public Result onClearConnections(Player playerEntity) {
        this.emitPos = null;
        this.timeline = null;

        updateBlock();
        return Result.SUCCESS;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (timeline != null) {
            tag.put("prestidigitation_timeline", ANCodecs.encode(PrestidigitationTimeline.CODEC.codec(), timeline));
        }
        if (emitPos != null) {
            tag.putLong("emitPos", emitPos.asLong());
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        timeline = null;
        emitPos = null;
        if (tag.contains("prestidigitation_timeline")) {
            timeline = ANCodecs.decode(PrestidigitationTimeline.CODEC.codec(), tag.getCompound("prestidigitation_timeline"));
        }

        if (tag.contains("emitPos")) {
            emitPos = BlockPos.of(tag.getLong("emitPos"));
        }
    }

    @Override
    public ResolveStatus onPreResolve(Level world, @NotNull LivingEntity shooter, HitResult result, Spell spell, SpellContext spellContext, AbstractEffect resolveEffect, SpellStats spellStats, SpellResolver spellResolver) {
        if (resolveEffect instanceof EffectPrestidigitation) {
            this.timeline = spell.particleTimeline().get(ParticleTimelineRegistry.PRESTIDIGITATION_TIMELINE.get());
            emitter = new ParticleEmitter(() -> this.emitPos.getCenter(), () -> new Vec2(0, 0), timeline.onTickEffect);
            updateBlock();
            return ResolveStatus.CONSUME;
        } else if (resolveEffect instanceof EffectDispel dispel) {
            this.timeline = null;
            updateBlock();
        }
        return ResolveStatus.CONTINUE;
    }

    AnimationController openCloseController;

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        openCloseController = new AnimationController<>(this, "openClose", (event) -> {

            event.getController().setAnimation(RawAnimation.begin().thenPlayAndHold(timeline == null ? "close" : "open"));
            return PlayState.CONTINUE;
        });
        controllers.add(openCloseController);
    }

    AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
