package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.entity.IDispellable;
import com.hollingsworth.arsnouveau.api.item.IWandable;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.SummonUtil;
import com.hollingsworth.arsnouveau.client.particle.GlowParticleData;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.tile.IAnimationListener;
import com.hollingsworth.arsnouveau.common.compat.PatchouliHandler;
import com.hollingsworth.arsnouveau.common.entity.debug.IDebugger;
import com.hollingsworth.arsnouveau.common.entity.debug.IDebuggerProvider;
import com.hollingsworth.arsnouveau.common.entity.statemachine.SimpleStateMachine;
import com.hollingsworth.arsnouveau.common.entity.statemachine.alakarkinos.DecideCrabActionState;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Alakarkinos extends PathfinderMob implements GeoEntity, IDispellable, ITooltipProvider, IWandable, IDebuggerProvider, IAnimationListener {

    public boolean tamed;
    public static final EntityDataAccessor<Optional<BlockPos>> HOME = SynchedEntityData.defineId(Alakarkinos.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
    public static final EntityDataAccessor<Boolean> HAS_HAT = SynchedEntityData.defineId(Alakarkinos.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> BLOWING_BUBBLES = SynchedEntityData.defineId(Alakarkinos.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Optional<BlockPos>> BLOWING_AT = SynchedEntityData.defineId(Alakarkinos.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
    public int findBlockCooldown;

    public boolean partyCrab = false;
    public BlockPos jukeboxPos = null;
    public BlockPos hatPos = null;

    public SimpleStateMachine stateMachine = new SimpleStateMachine(new DecideCrabActionState(this));

    public Alakarkinos(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        reloadGoals();
        this.tamed = true;
    }

    @Override
    public void setRecordPlayingNearby(BlockPos pos, boolean hasSound) {
        super.setRecordPlayingNearby(pos, hasSound);
        this.partyCrab = hasSound;
        this.jukeboxPos = pos;
    }

    @Override
    public void tick() {
        super.tick();
        if (!level.isClientSide) {
            stateMachine.tick();
            SummonUtil.healOverTime(this);
            if (findBlockCooldown > 0) {
                findBlockCooldown--;
            }
        } else {

            if (blowingBubbles()) {
                var optPos = this.entityData.get(BLOWING_AT);
                if (optPos.isEmpty()) {
                    return;
                }
                BlockPos to = optPos.get();
                Vec3 towards = new Vec3(to.getX() + 0.5, to.getY() + 0.5, to.getZ() + 0.5);
                var xRot = this.xRot;
                var yRot = this.yRot;
                // Spawn particles from this mob to the target
                float randScale = 0.2f;
                Vec3 from = new Vec3(this.getX() + ParticleUtil.inRange(-randScale, randScale), this.getY() + 0.75 + ParticleUtil.inRange(-randScale, randScale), this.getZ() + ParticleUtil.inRange(-randScale, randScale));
                Vec3 dir = towards.subtract(from).normalize();
                Vec3 pos = from.add(dir.scale(0.5));
                Vec3 motion = dir.scale(0.1);
                level.addParticle(GlowParticleData.createData(new ParticleColor(
                        50,
                        50,
                        200
                )), pos.x, pos.y, pos.z, motion.x, motion.y, motion.z);


            }
        }
    }


    // Cannot add conditional goals in RegisterGoals as it is final and called during the MobEntity super.
    protected void reloadGoals() {
        if (this.level.isClientSide())
            return;
        this.goalSelector.availableGoals.clear();
        for (WrappedGoal goal : getGoals()) {
            this.goalSelector.addGoal(goal.getPriority(), goal.getGoal());
        }
    }

    public List<WrappedGoal> getGoals() {
        List<WrappedGoal> list = new ArrayList<>();
        list.add(new WrappedGoal(0, new FloatGoal(this)));
        if (!tamed) {
            list.add(new WrappedGoal(4, new LookAtPlayerGoal(this, Player.class, 3.0F, 0.02F)));
            list.add(new WrappedGoal(4, new LookAtPlayerGoal(this, Mob.class, 8.0F)));
//            list.add(new WrappedGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.2D)));
            list.add(new WrappedGoal(0, new FloatGoal(this)));
        }
        return list;
    }

    @Override
    public EntityType<?> getType() {
        return ModEntities.ALAKARKINOS_TYPE.get();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder pBuilder) {
        super.defineSynchedData(pBuilder);
        pBuilder.define(HOME, Optional.empty());
        pBuilder.define(HAS_HAT, true);
        pBuilder.define(BLOWING_BUBBLES, false);
        pBuilder.define(BLOWING_AT, Optional.empty());
    }

    public boolean hasHat() {
        return this.entityData.get(HAS_HAT);
    }

    public void setHat(boolean hasHat) {
        this.entityData.set(HAS_HAT, hasHat);
    }

    public void setBlowingBubbles(boolean blowingBubbles) {
        this.entityData.set(BLOWING_BUBBLES, blowingBubbles);
    }

    public boolean blowingBubbles() {
        return this.entityData.get(BLOWING_BUBBLES);
    }

    public @Nullable BlockPos getHome() {
        return this.entityData.get(HOME).orElse(null);
    }

    @Override
    public void getTooltip(List<Component> tooltip) {

    }

    @Override
    public void onFinishedConnectionLast(@Nullable BlockPos storedPos, @Nullable LivingEntity storedEntity, Player playerEntity) {
        if (playerEntity.level.getCapability(Capabilities.ItemHandler.BLOCK, storedPos, null) != null) {
            this.entityData.set(HOME, Optional.ofNullable(storedPos));
            PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.alakarkinos.set_home"));
        }
    }

    @Override
    public boolean onDispel(@NotNull LivingEntity caster) {
        return false;
    }

    @Override
    public IDebugger getDebugger() {
        return null;
    }

    AnimationController placeHat;

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "walkController", 1, (event) -> {
            if (blowingBubbles()) {
                return PlayState.STOP;
            }
            if (event.isMoving() || (level.isClientSide && PatchouliHandler.isPatchouliWorld())) {
                event.getController().setAnimation(RawAnimation.begin().thenPlay("run"));
                return PlayState.CONTINUE;
            }
            return PlayState.STOP;
        }));

        controllers.add(new AnimationController<>(this, "idleController", 1, (event) -> {
            if (blowingBubbles()) {
                return PlayState.STOP;
            }
            if (!event.isMoving()) {
                event.getController().setAnimation(RawAnimation.begin().thenPlay("idle"));
                return PlayState.CONTINUE;
            }
            return PlayState.STOP;
        }));

        controllers.add(new AnimationController<>(this, "danceController", 1, (event) -> {
            if (blowingBubbles()) {
                return PlayState.STOP;
            }
            if ((this.partyCrab && this.jukeboxPos != null && BlockUtil.distanceFrom(position, jukeboxPos) <= 8)) {
                event.getController().setAnimation(RawAnimation.begin().thenPlay("dance"));
                return PlayState.CONTINUE;
            }
            return PlayState.STOP;
        }));

        controllers.add(new AnimationController<>(this, "blowBubbles", 1, (event) -> {
            if (blowingBubbles()) {
                event.getController().setAnimation(RawAnimation.begin().thenPlay("bubble_blow"));
                return PlayState.CONTINUE;
            }
            return PlayState.STOP;
        }));

        placeHat = new AnimationController<>(this, "placeHatController", 1, (event) -> PlayState.CONTINUE);
        controllers.add(placeHat);
    }


    AnimatableInstanceCache manager = GeckoLibUtil.createInstanceCache(this);

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return manager;
    }

    @Override
    public boolean save(CompoundTag pCompound) {
        if (this.entityData.get(HOME).isPresent()) {
            pCompound.putLong("home", this.entityData.get(HOME).get().asLong());
        }
        pCompound.putInt("findBlockCooldown", findBlockCooldown);
        if (this.hatPos != null) {
            pCompound.putLong("hatPos", this.hatPos.asLong());
        }
        return super.save(pCompound);
    }

    @Override
    public void load(CompoundTag pCompound) {
        super.load(pCompound);
        if (pCompound.contains("home")) {
            this.entityData.set(HOME, Optional.of(BlockPos.of(pCompound.getLong("home"))));
        }
        findBlockCooldown = pCompound.getInt("findBlockCooldown");
        if (pCompound.contains("hatPos")) {
            this.hatPos = BlockPos.of(pCompound.getLong("hatPos"));
        }
    }

    @Override
    public void startAnimation(int arg) {
        if (arg == 0) {
            if (placeHat == null) {
                return;
            }
            placeHat.forceAnimationReset();
            placeHat.setAnimation(RawAnimation.begin().thenPlay("place_hat"));
        } else if (arg == 1) {
            if (placeHat == null) {
                return;
            }
            placeHat.forceAnimationReset();
            placeHat.setAnimation(RawAnimation.begin().thenPlay("bubble_blow"));
        }
    }

    @Override
    public boolean hurt(@NotNull DamageSource pSource, float pAmount) {
        return SummonUtil.canSummonTakeDamage(pSource) && super.hurt(pSource, pAmount);
    }
}
