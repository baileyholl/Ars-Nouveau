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
import com.hollingsworth.arsnouveau.setup.config.ServerConfig;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
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
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.object.PlayState;
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
        if (timeline != null && level.isClientSide() && emitPos != null) {
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
            playerEntity.displayClientMessage(Component.translatable("ars_nouveau.connection.range", ServerConfig.DECOR_BLOSSOM_RANGE.get()), false);
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
    protected void saveAdditional(ValueOutput tag) {
        super.saveAdditional(tag);
        if (timeline != null) {
            tag.store("prestidigitation_timeline", PrestidigitationTimeline.CODEC.codec(), timeline);
        }
        if (emitPos != null) {
            tag.putLong("emitPos", emitPos.asLong());
        }
    }

    @Override
    protected void loadAdditional(ValueInput tag) {
        super.loadAdditional(tag);
        timeline = null;
        emitPos = null;
        timeline = tag.read("prestidigitation_timeline", PrestidigitationTimeline.CODEC.codec()).orElse(null);
        tag.getLong("emitPos").ifPresent(l -> emitPos = BlockPos.of(l));
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

    AnimationController<DecorBlossomTile> openCloseController;

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        openCloseController = new AnimationController<DecorBlossomTile>("openClose", (event) -> {

            event.controller().setAnimation(RawAnimation.begin().thenPlayAndHold(timeline == null ? "close" : "open"));
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
