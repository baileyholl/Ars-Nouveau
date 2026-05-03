package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.common.entity.statemachine.arcano_boss.InitArcanoState;
import com.hollingsworth.arsnouveau.setup.registry.DataSerializers;
import com.hollingsworth.arsnouveau.setup.registry.ModEntities;
import com.hollingsworth.nuggets.common.state_machine.SimpleStateMachine;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ByIdMap;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

import java.util.function.IntFunction;

public class ArcanoBoss extends Mob {
    private static final EntityDataAccessor<ArcanoBossState> ARCANO_POSE = SynchedEntityData.defineId(ArcanoBoss.class, DataSerializers.ARCANO_POSE.get());

    private SimpleStateMachine stateMachine = new SimpleStateMachine(new InitArcanoState(this));
    public AnimationState flight = new AnimationState();
    public AnimationState idle = new AnimationState();
    public AnimationState swingStaff = new AnimationState();
    public AnimationState spinStaff = new AnimationState();
    public AnimationState spinStaff2 = new AnimationState();


    public ArcanoBoss(EntityType<? extends Mob> entityType, Level level) {
        super(entityType, level);
        setArcanoPose(ArcanoBossState.IDLE);

    }

    public ArcanoBoss(Level level) {
        this(ModEntities.ARCANO_BOSS.get(), level);

    }

    public static AttributeSupplier.Builder attributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 600d)
                .add(Attributes.MOVEMENT_SPEED, 0.2d)
                .add(Attributes.FOLLOW_RANGE, 16D)
                .add(Attributes.ATTACK_DAMAGE, 10.5d);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ARCANO_POSE, ArcanoBossState.IDLE);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.isDeadOrDying() && this.isEffectiveAi() && level instanceof ServerLevel serverLevel) {
            this.setPos(16, 1, 16);
            stateMachine.tick();
        }
        if (level.isClientSide) {
            this.idle.startIfStopped(this.tickCount);
            this.flight.startIfStopped(this.tickCount);
        }
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        if (ARCANO_POSE.equals(key)) {
            ArcanoBossState pose = getArcanoPose();
            if (pose == ArcanoBossState.SWING) {
                this.swingStaff.start(this.tickCount);
            } else {
                this.swingStaff.stop();
            }
        }

        super.onSyncedDataUpdated(key);
    }

    private void setAnimationState(ArcanoBossState pose, ArcanoBossState poseToCheckFor, AnimationState animationState) {
        setAnimationState(pose, poseToCheckFor, animationState, null, 0, 0);
    }

    private void setAnimationState(ArcanoBossState pose, ArcanoBossState poseToCheckFor, AnimationState animationState, int tickCount) {
        setAnimationState(pose, poseToCheckFor, animationState, tickCount, null, 0, 0);
    }

    private void setAnimationState(ArcanoBossState pose, ArcanoBossState poseToCheckFor, AnimationState animationState, ParticleOptions particleType, int particleLifeSpan, double yOffset) {
        setAnimationState(pose, poseToCheckFor, animationState, this.tickCount, particleType, particleLifeSpan, yOffset);
    }

    private void setAnimationState(ArcanoBossState pose, ArcanoBossState poseToCheckFor, AnimationState animationState, int tickCount, ParticleOptions particleType, int particleLifeSpan, double yOffset) {
        if (pose == poseToCheckFor) {
            if (!animationState.isStarted()) {
                animationState.start(tickCount);

                if (particleType != null && this.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(
                            particleType,
                            this.getX(),
                            this.getBoundingBox().maxY + yOffset,
                            this.getZ(),
                            0,
                            1,
                            1,
                            1,
                            particleLifeSpan);
                }
            }
        } else {
//            animationState.stop();
        }
        this.flight.startIfStopped(3000);
    }

    public void setArcanoPose(ArcanoBossState rootminState) {
        this.entityData.set(ARCANO_POSE, rootminState);
    }

    public ArcanoBossState getArcanoPose() {
        return this.entityData.get(ARCANO_POSE);
    }

    public enum ArcanoBossState {
        IDLE(0),
        FLIGHT(1),
        SWING(2),
        SPIN_STAFF(3),
        SPIN_STAFF2(4);

        public static final IntFunction<ArcanoBossState> BY_ID = ByIdMap.continuous(ArcanoBossState::id, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
        public static final StreamCodec<ByteBuf, ArcanoBossState> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, ArcanoBossState::id);
        private final int id;

        private ArcanoBossState(final int j) {
            this.id = j;
        }

        public int id() {
            return this.id;
        }
    }
}
