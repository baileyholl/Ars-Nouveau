package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.api.entity.ISummon;
import com.hollingsworth.arsnouveau.api.spell.EntitySpellResolver;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.wrapped_caster.LivingCaster;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleLineData;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.entity.goal.ConditionalMeleeGoal;
import com.hollingsworth.arsnouveau.common.entity.goal.chimera.*;
import com.hollingsworth.arsnouveau.common.potions.SnareEffect;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectDelay;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectKnockback;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectLaunch;
import com.hollingsworth.arsnouveau.common.spell.method.MethodTouch;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ModEntities;
import com.hollingsworth.arsnouveau.setup.registry.ModPotions;
import com.hollingsworth.arsnouveau.setup.registry.SoundRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidType;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;

public class WildenChimera extends Monster implements GeoEntity {
    private final ServerBossEvent bossEvent = (ServerBossEvent) (new ServerBossEvent(this.getDisplayName(), BossEvent.BossBarColor.PURPLE, BossEvent.BossBarOverlay.PROGRESS)).setDarkenScreen(true).setCreateWorldFog(true);
    public static final EntityDataAccessor<Boolean> HAS_SPIKES = SynchedEntityData.defineId(WildenChimera.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> HAS_HORNS = SynchedEntityData.defineId(WildenChimera.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> HAS_WINGS = SynchedEntityData.defineId(WildenChimera.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Integer> PHASE = SynchedEntityData.defineId(WildenChimera.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Boolean> DEFENSIVE_MODE = SynchedEntityData.defineId(WildenChimera.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> PHASE_SWAPPING = SynchedEntityData.defineId(WildenChimera.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> IS_FLYING = SynchedEntityData.defineId(WildenChimera.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> IS_HOWLING = SynchedEntityData.defineId(WildenChimera.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> IS_DIVING = SynchedEntityData.defineId(WildenChimera.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> IS_RAMMING = SynchedEntityData.defineId(WildenChimera.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> RAM_PREP = SynchedEntityData.defineId(WildenChimera.class, EntityDataSerializers.BOOLEAN);

    public boolean initMusic;

    public boolean isRamGoal;
    public int summonCooldown;
    public int diveCooldown;
    public int spikeCooldown;
    public int ramCooldown;
    public int rageTimer;
    public boolean diving;
    public int swapTicks;

    public FlyingPathNavigation flyingNavigator;
    protected final WaterBoundPathNavigation waterNavigation;
    protected final GroundPathNavigation groundNavigation;

    public WildenChimera(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        moveControl = new ChimeraMoveController(this, true);
        setPersistenceRequired();
        FlyingPathNavigation flyingpathnavigator = new FlyingPathNavigation(this, level);
        flyingpathnavigator.setCanOpenDoors(true);
        flyingpathnavigator.setCanFloat(false);
        flyingpathnavigator.setCanPassDoors(true);
        this.setPathfindingMalus(PathType.WATER, 0.0F);
        this.flyingNavigator = flyingpathnavigator;
        this.waterNavigation = new WaterBoundPathNavigation(this, level);
        this.groundNavigation = new GroundPathNavigation(this, level);
        rageTimer = 300;
        this.xpReward = 75;
    }

    public WildenChimera(Level level) {
        this(ModEntities.WILDEN_BOSS.get(), level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        this.goalSelector.addGoal(5, new ConditionalMeleeGoal(this, 1.2d, true, ()-> !this.isHowling() && !this.isFlying() && !this.isDefensive() && !this.isDiving() && !this.getPhaseSwapping() && !isRamming() && !isRamPrep()));
        this.goalSelector.addGoal(3, new ChimeraSummonGoal(this));
        this.targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.goalSelector.addGoal(1, new ChimeraRageGoal(this));
        this.goalSelector.addGoal(3, new ChimeraLeapRamGoal(this));
        this.goalSelector.addGoal(3, new ChimeraRamGoal(this));
        this.goalSelector.addGoal(3, new ChimeraDiveGoal(this));
        this.goalSelector.addGoal(3, new ChimeraSpikeGoal(this));
    }


    AnimationController<WildenChimera> crouchController;

    public boolean isPushedByFluid() {
        return !this.isSwimming();
    }

    @Override
    public boolean isPushedByFluid(FluidType type) {
        return !this.isSwimming();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar animatableManager) {
        animatableManager.add(new AnimationController<>(this, "walkController", 1, e ->{
            if (!isDefensive() && e.isMoving() && !isFlying() && !isHowling() && !isSwimming() && !isRamPrep() && !isRamming()){
                e.getController().setAnimation(RawAnimation.begin().thenPlay("run"));
                return PlayState.CONTINUE;
            }

            return PlayState.STOP;
        }));
        crouchController = new AnimationController<>(this, "crouchController", 1, event -> {
            if (isDefensive() && !isFlying() && !this.isHowling()){
                event.getController().setAnimation(RawAnimation.begin().thenPlay("defending"));
                return PlayState.CONTINUE;
            }
            return PlayState.STOP;
        });

        animatableManager.add(crouchController);
        animatableManager.add(new AnimationController<>(this, "idleController", 1, (event ->{
            if(!event.isMoving() && !isDefensive() && !isFlying() && !isHowling() && !isRamPrep() && !isRamming()){
                event.getController().setAnimation(RawAnimation.begin().thenPlay("idle"));
                return PlayState.CONTINUE;
            }
            return PlayState.STOP;
        })));
        animatableManager.add(new AnimationController<>(this, "flyController", 1, (event) ->{
            if(isFlying() && !isDiving()){
                event.getController().setAnimation(RawAnimation.begin().thenPlay("fly_rising"));
                return PlayState.CONTINUE;
            }
            return PlayState.STOP;
        }));
        animatableManager.add(new AnimationController<>(this, "diveController", 1, (event) ->{
            if(isDiving()){
                event.getController().setAnimation(RawAnimation.begin().thenPlay("dive"));
                return PlayState.CONTINUE;
            }
            return PlayState.STOP;
        }));
        animatableManager.add(new AnimationController<>(this, "howlController", 1, e ->{
            if (isHowling()) {
                e.getController().setAnimation(RawAnimation.begin().thenPlay("roar"));
                return PlayState.CONTINUE;
            }
            e.getController().forceAnimationReset();
            return PlayState.STOP;
        }));
        animatableManager.add(new AnimationController<>(this, "swimController", 1, e ->{
            if (!isDefensive() && e.isMoving() && !isFlying() && !isHowling() && isSwimming()){
                e.getController().setAnimation(RawAnimation.begin().thenPlay("swim"));
                return PlayState.CONTINUE;
            }

            return PlayState.STOP;
        }));
        animatableManager.add(new AnimationController<>(this, "ramController", 1, (event -> {
            if(isRamming() && !isRamPrep()){
                if(!this.hasWings()){
                    event.getController().setAnimation(RawAnimation.begin().thenPlay("charge"));
                }
                return PlayState.CONTINUE;
            }
            return PlayState.STOP;
        })));
        animatableManager.add(new AnimationController<>(this, "ramPrep", 1, (event -> {
            if(isRamPrep() && !isRamming()){
                if(this.hasWings()){
                    event.getController().setAnimation(RawAnimation.begin().thenPlay("wing_charge_prep"));
                }else{
                    event.getController().setAnimation(RawAnimation.begin().thenPlay("charge_prep"));
                }
                return PlayState.CONTINUE;
            }
            event.getController().forceAnimationReset();
            return PlayState.STOP;
        })));
    }

    public void updateSwimming() {
        if (!this.level.isClientSide) {
            if (this.isEffectiveAi() && this.isInWater() && this.wantsToSwim()) {
                this.navigation = this.waterNavigation;
                this.setSwimming(true);
            } else {
                this.navigation = this.groundNavigation;
                this.setSwimming(false);
            }
        }

    }

    @Override
    public void tick() {
        super.tick();
        if(level.isClientSide && !initMusic){
            initMusic = true;
            ((Runnable) () -> ChimeraMusic.play(WildenChimera.this)).run();
        }

        //   this.goalSelector.getRunningGoals().forEach(g -> System.out.println(g.getGoal().toString()));
        if (!level.isClientSide && isDefensive()){
            this.getNavigation().stop();
        }
        if (level.isClientSide && isFlying() && random.nextInt(18) == 0) {

            this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.BAT_LOOP, this.getSoundSource(),
                    0.95F + this.random.nextFloat() * 0.05F,
                    0.3f + this.random.nextFloat() * 0.05F,
                    false);

        }

        if (!level.isClientSide && this.isInLava() && this.level.getGameTime() % 10 == 0) {
            this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20, 4));
            this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 20, 3));
            this.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 20));
            this.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 20, 3));

        }

        if (this.isFlying()) {
            this.navigation.stop();
            flyingNavigator.tick();
        }

        if (!level.isClientSide) {
            if (summonCooldown > 0)
                summonCooldown--;
            if (diveCooldown > 0) {
                diveCooldown--;
                if (this.isInLava() || this.isInWater())
                    spikeCooldown -= 2;
            }
            if (spikeCooldown > 0) {
                spikeCooldown--;

            }
            if (ramCooldown > 0)
                ramCooldown--;
        }

        if (!level.isClientSide && getPhaseSwapping() && !this.dead) {
            if (swapTicks < 60) {
                swapTicks++;
                this.navigation.stop();
            } else {
                this.removeAllEffects();
                this.setPhaseSwapping(false);
                for (LivingEntity e : level.getEntitiesOfClass(Player.class, new AABB(this.blockPosition()).inflate(5))) {
                    EntitySpellResolver resolver = new EntitySpellResolver(new SpellContext(level, new Spell().add(MethodTouch.INSTANCE)
                            .add(EffectLaunch.INSTANCE)
                            .add(AugmentAmplify.INSTANCE, 2)
                            .add(EffectDelay.INSTANCE)
                            .add(EffectKnockback.INSTANCE)
                            .add(AugmentAmplify.INSTANCE, 2)
                            , this, new LivingCaster(this)));
                    resolver.onCastOnEntity(ItemStack.EMPTY, e, InteractionHand.MAIN_HAND);
                }
                getRandomUpgrade();
                ParticleUtil.spawnPoof((ServerLevel) level, blockPosition().above());
                if (getPhase() == 1)
                    rageTimer = 200;
                swapTicks = 0;
            }
        }

        if (getPhaseSwapping() && level.isClientSide) {
            spawnPhaseParticles(blockPosition(), level, getPhase());
        }
    }

    public static void spawnPhaseParticles(BlockPos pos, Level level, int multiplier) {
        if (!level.isClientSide)
            return;
        int baseAge = 40;
        float scaleAge = (float) ParticleUtil.inRange(0.1, 0.2);
        for (int i = 0; i < 10 * (Math.min(1, multiplier)); i++) {
            Vec3 particlePos = new Vec3(pos.getX(), pos.getY() + 1, pos.getZ()).add(0.5, 0.5, 0.5);
            particlePos = particlePos.add(ParticleUtil.pointInSphere().multiply(3.0, 3.0, 3.0));
            level.addParticle(ParticleLineData.createData(ParticleColor.makeRandomColor(255, 255, 255, level.random), scaleAge, baseAge + level.random.nextInt(20)),
                    particlePos.x(), particlePos.y(), particlePos.z(),
                    pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        }
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel p_348683_, DamageSource p_21385_, boolean p_21387_) {
        super.dropCustomDeathLoot(p_348683_, p_21385_, p_21387_);
        ItemEntity itementity = this.spawnAtLocation(ItemsRegistry.WILDEN_TRIBUTE.get());
        if (itementity != null) {
            itementity.setExtendedLifetime();
        }
    }

    protected void playStepSound(BlockPos p_180429_1_, BlockState p_180429_2_) {
        this.playSound(SoundEvents.POLAR_BEAR_STEP, 0.15F + (random.nextFloat() * 0.3f), 0.8F + random.nextFloat() * 0.1f);
    }

    @Override
    protected SoundEvent getSwimSound() {
        return SoundEvents.HOSTILE_SWIM;
    }

    @Override
    public boolean isSilent() {
        return false;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
        return SoundEvents.POLAR_BEAR_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.POLAR_BEAR_DEATH;
    }

    public void resetCooldowns() {
        spikeCooldown = 0;
        ramCooldown = 0;
        diveCooldown = 0;
        summonCooldown = 0;
    }

    @Override
    public void heal(float p_70691_1_) {
        this.setHealth(this.getHealth() + p_70691_1_);
    }

    public boolean canDive() {
        return !isRamGoal && diveCooldown <= 0 && hasWings() && !getPhaseSwapping() && !isFlying() && this.onGround() && !isDefensive();
    }

    public boolean canSpike() {
        return !isRamGoal && spikeCooldown <= 0 && hasSpikes() && !getPhaseSwapping() && !isFlying() && this.onGround() && this.getTarget() != null;
    }

    public boolean canRam(boolean withWings) {
        if(withWings != hasWings()){
            return false;
        }
        return !isRamGoal && ramCooldown <= 0 && hasHorns() && !getPhaseSwapping() && !isFlying() && !isDefensive() && getTarget() != null && getTarget().onGround() && this.onGround();
    }

    public boolean canSummon() {
        return !isRamGoal && getTarget() != null && summonCooldown <= 0 && !isFlying() && !getPhaseSwapping() && !isDefensive() && this.onGround();
    }

    public boolean canAttack() {
        return !isRamGoal && getTarget() != null && this.getHealth() >= 1 && !this.getPhaseSwapping() && !isFlying() && !isDefensive();
    }

    public boolean canRage() {
        return !getPhaseSwapping() && !isRamGoal;
    }

    public void getRandomUpgrade() {
        ArrayList<Integer> upgrades = new ArrayList<>();
        if (!this.hasWings())
            upgrades.add(0);
        if (!this.hasSpikes())
            upgrades.add(1);
        if (!this.hasHorns())
            upgrades.add(2);
        if (upgrades.isEmpty())
            return;
        int upgrade = upgrades.get(random.nextInt(upgrades.size()));
        switch (upgrade) {
            case 0 -> setWings(true);
            case 1 -> setSpikes(true);
            case 2 -> setHorns(true);
        }
    }

    public void travel(Vec3 pTravelVector) {
        if (this.isEffectiveAi() && this.isInWater() && this.wantsToSwim()) {
            this.moveRelative(0.01F, pTravelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
        } else {
            super.travel(pTravelVector);
        }

    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypes.CACTUS) || source.is(DamageTypes.SWEET_BERRY_BUSH) || source.is(DamageTypes.IN_WALL)  || source.is(DamageTypes.LAVA) || source.is(DamageTypes.DROWN))
            return false;
        if (source.type().msgId().equals("cold"))
            amount = amount / 2;
        if (this.getPhaseSwapping())
            return false;

        Entity entity = source.getEntity();
        if (entity instanceof LivingEntity entity1 && !entity.equals(this)) {
            if (isDefensive() && !source.type().msgId().equals("thorns")) {
                if (!source.is(DamageTypeTags.BYPASSES_ARMOR) && BlockUtil.distanceFrom(entity.position, position) <= 3) {
                    entity.hurt(level.damageSources().thorns(this), 6.0f);
                }
            }

            // Omit our summoned sources that might aggro or accidentally hurt us
            if (entity1 instanceof WildenStalker || entity1 instanceof WildenGuardian || entity instanceof WildenHunter
                    || (entity instanceof ISummon && ((ISummon) entity).getOwnerUUID() != null && ((ISummon) entity).getOwnerUUID().equals(this.getUUID()))
                    || (entity1 instanceof SummonWolf && ((SummonWolf) entity1).isWildenSummon))
                return false;
        }

        if (isDefensive())
            return false;
        var threshold = this.getMaxHealth() / 4;
        var nextMin = this.getMaxHealth() - threshold * (1 + getPhase());
        boolean res = super.hurt(source, amount);
        if (!this.level.isClientSide && this.getHealth() <= nextMin && getPhase() < 3) {
            this.setPhaseSwapping(true);
            this.setPhase(this.getPhase() + 1);
            this.getNavigation().stop();
            this.setHealth(nextMin);
            this.dead = false;
            this.setFlying(false);
            this.setDefensiveMode(false);
            isRamGoal = false;
        }
        return res;
    }

    @Override
    public boolean isDeadOrDying() {
        return getPhase() >= 3 && this.getHealth() <= 0;
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
    }

    protected boolean canRide(Entity p_184228_1_) {
        return false;
    }

    @Override
    public void checkDespawn() {
        if (this.level.getDifficulty() == Difficulty.PEACEFUL && this.shouldDespawnInPeaceful()) {
            this.remove(RemovalReason.DISCARDED);
        } else {
            this.noActionTime = 0;
        }
    }


    public void startSeenByPlayer(ServerPlayer p_184178_1_) {
        super.startSeenByPlayer(p_184178_1_);
        this.bossEvent.addPlayer(p_184178_1_);
    }

    public void stopSeenByPlayer(ServerPlayer p_184203_1_) {
        super.stopSeenByPlayer(p_184203_1_);
        this.bossEvent.removePlayer(p_184203_1_);
    }

    public boolean canChangeDimensions() {
        return false;
    }

    public boolean canBeAffected(MobEffectInstance instance) {
        MobEffect effect = instance.getEffect().value();
        if (instance.getEffect() instanceof SnareEffect)
            return false;

        if (effect == MobEffects.MOVEMENT_SLOWDOWN)
            instance = new MobEffectInstance(instance.getEffect(), 1, 0);

        if (effect == ModPotions.GRAVITY_EFFECT.get())
            instance = new MobEffectInstance(instance.getEffect(), Math.min(instance.getDuration(), 100), 0);
        return super.canBeAffected(instance);
    }

    public int getCooldownModifier() {
        return 300 / (getPhase() + 1);
    }

    AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder pBuilder) {
        super.defineSynchedData(pBuilder);
        pBuilder.define(HAS_HORNS, false);
        pBuilder.define(HAS_SPIKES, false);
        pBuilder.define(HAS_WINGS, false);
        pBuilder.define(PHASE, 1);
        pBuilder.define(DEFENSIVE_MODE, false);
        pBuilder.define(PHASE_SWAPPING, false);
        pBuilder.define(IS_FLYING, false);
        pBuilder.define(IS_HOWLING, false);
        pBuilder.define(IS_DIVING, false);
        pBuilder.define(IS_RAMMING, false);
        pBuilder.define(RAM_PREP, false);
    }

    public boolean isFlying() {
        return entityData.get(IS_FLYING);
    }

    public void setFlying(boolean flying) {
        entityData.set(IS_FLYING, flying);
    }

    public boolean isHowling() {
        return entityData.get(IS_HOWLING) || this.getPhaseSwapping();
    }

    public void setHowling(boolean howling) {
        entityData.set(IS_HOWLING, howling);
    }

    public boolean hasHorns() {
        return entityData.get(HAS_HORNS);
    }

    public void setHorns(boolean hasHorns) {
        entityData.set(HAS_HORNS, hasHorns);
    }

    public boolean hasSpikes() {
        return entityData.get(HAS_SPIKES);
    }

    public void setSpikes(boolean hasSpikes) {
        entityData.set(HAS_SPIKES, hasSpikes);
    }

    public boolean hasWings() {
        return entityData.get(HAS_WINGS);
    }

    public void setWings(boolean hasWings) {
        entityData.set(HAS_WINGS, hasWings);
    }

    public boolean isDefensive() {
        return entityData.get(DEFENSIVE_MODE);
    }

    public void setDefensiveMode(boolean defensiveMode) {
        entityData.set(DEFENSIVE_MODE, defensiveMode);
    }

    public int getPhase() {
        return entityData.get(PHASE);
    }

    public void setPhase(int phase) {
        entityData.set(PHASE, phase);
    }

    public boolean getPhaseSwapping() {
        return entityData.get(PHASE_SWAPPING);
    }

    public void setPhaseSwapping(boolean swapping) {
        entityData.set(PHASE_SWAPPING, swapping);
    }

    public void setDiving(boolean diving) {
        entityData.set(IS_DIVING, diving);
    }

    public boolean isDiving() {
        return entityData.get(IS_DIVING);
    }

    public boolean isRamming() {
        return entityData.get(IS_RAMMING);
    }

    public void setRamming(boolean ramming) {
        entityData.set(IS_RAMMING, ramming);
    }

    public boolean isRamPrep(){
        return entityData.get(RAM_PREP);
    }

    public void setRamPrep(boolean ramPrep) {
        entityData.set(RAM_PREP, ramPrep);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        setHorns(tag.getBoolean("horns"));
        setSpikes(tag.getBoolean("spikes"));
        setWings(tag.getBoolean("wings"));
        setPhase(tag.getInt("phase"));
        setDefensiveMode(tag.getBoolean("defensive"));
        setPhaseSwapping(tag.getBoolean("swapping"));
        summonCooldown = tag.getInt("summonCooldown");
        diveCooldown = tag.getInt("diveCooldown");
        spikeCooldown = tag.getInt("spikeCooldown");
        ramCooldown = tag.getInt("ramCooldown");
        rageTimer = tag.getInt("rage");
        swapTicks = tag.getInt("swapTicks");
    }

    @Override
    public boolean save(CompoundTag tag) {
        tag.putBoolean("spikes", hasSpikes());
        tag.putBoolean("horns", hasHorns());
        tag.putBoolean("wings", hasWings());
        tag.putInt("phase", getPhase());
        tag.putBoolean("defensive", isDefensive());
        tag.putInt("summonCooldown", summonCooldown);
        tag.putInt("diveCooldown", diveCooldown);
        tag.putInt("spikeCooldown", spikeCooldown);
        tag.putInt("ramCooldown", ramCooldown);
        tag.putBoolean("swapping", getPhaseSwapping());
        tag.putInt("rage", rageTimer);
        tag.putInt("swapTicks", swapTicks);
        return super.save(tag);
    }

    @Override
    protected void checkFallDamage(double p_184231_1_, boolean p_184231_3_, BlockState p_184231_4_, BlockPos p_184231_5_) {
        super.checkFallDamage(p_184231_1_, p_184231_3_, p_184231_4_, p_184231_5_);
        if (hasWings())
            this.fallDistance = 0;
        this.fallDistance = Math.min(fallDistance, 10);
    }

    public boolean wantsToSwim(){
        LivingEntity livingentity = this.getTarget();
        return livingentity != null && livingentity.isInWater();
    }

    public enum Animations {
        ATTACK,
        HOWL,
        CHARGE,
        FLYING,
        DIVE_BOMB
    }

    @Override
    public PathNavigation getNavigation() {
        return this.isFlying() ? flyingNavigator : super.getNavigation();
    }

    public Vec3 orbitOffset = Vec3.ZERO;

    public static class ChimeraMoveController extends MoveControl {

        private final boolean hoversInPlace;
        private final WildenChimera chimera;

        public ChimeraMoveController(WildenChimera chimera, boolean hoversInPlace) {
            super(chimera);
            this.hoversInPlace = hoversInPlace;
            this.chimera = chimera;
        }

        @Override
        public void tick() {
            WildenChimera chimera = (WildenChimera) this.mob;
            if (chimera.isFlying()) {
                if (chimera.diving) {
                    diveTick();
                } else {
                    flyTick();
                }
            }else if(this.chimera.wantsToSwim() && this.chimera.isInWater()){
                swimTick();
            } else {
                super.tick();
            }
        }

        // Drowned movement
        public void swimTick(){
            LivingEntity livingentity = this.chimera.getTarget();
            if (livingentity != null && livingentity.getY() > this.chimera.getY()) {
                this.chimera.setDeltaMovement(this.chimera.getDeltaMovement().add(0.0D, 0.02D, 0.0D));
            }

            if (this.operation != MoveControl.Operation.MOVE_TO || this.chimera.getNavigation().isDone()) {
                this.chimera.setSpeed(0.0F);
                return;
            }

            double d0 = this.wantedX - this.chimera.getX();
            double d1 = this.wantedY - this.chimera.getY();
            double d2 = this.wantedZ - this.chimera.getZ();
            double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
            d1 /= d3;
            float f = (float)(Mth.atan2(d2, d0) * (double)(180F / (float)Math.PI)) - 90.0F;
            this.chimera.setYRot(this.rotlerp(this.chimera.getYRot(), f, 90.0F));
            this.chimera.yBodyRot = this.chimera.getYRot();
            float f1 = (float)(this.speedModifier * this.chimera.getAttributeValue(Attributes.MOVEMENT_SPEED));
            float f2 = Mth.lerp(0.25F, this.chimera.getSpeed(), f1);
            this.chimera.setSpeed(f2);
            this.chimera.setDeltaMovement(this.chimera.getDeltaMovement().add((double)f2 * d0 * 0.08, (double)f2 * d1 * 0.13D, (double)f2 * d2 * 0.08));

        }

        // Copy from FlyingMovementController
        public void flyTick() {
            if (this.operation == MoveControl.Operation.MOVE_TO) {
                this.operation = MoveControl.Operation.WAIT;
                this.mob.setNoGravity(true);
                double d0 = this.wantedX - this.mob.getX();
                double d1 = this.wantedY - this.mob.getY();
                double d2 = this.wantedZ - this.mob.getZ();
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                if (d3 < (double) 2.5000003E-7F) {
                    this.mob.setYya(0.0F);
                    this.mob.setZza(0.0F);
                    return;
                }
                float f1;
                if (this.mob.onGround()) {
                    f1 = (float) (this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED));
                } else {
                    f1 = (float) (this.speedModifier * this.mob.getAttributeValue(Attributes.FLYING_SPEED));
                }
                this.mob.setSpeed(f1);
                this.mob.setYya(d1 > 0.0D ? f1 : -f1);
            } else {
                if (!this.hoversInPlace) {
                    this.mob.setNoGravity(false);
                }

                this.mob.setYya(0.0F);
                this.mob.setZza(0.0F);
            }

        }

        public void diveTick() {
            WildenChimera mob = (WildenChimera) this.mob;
            double posX = mob.getX();
            double posY = mob.getY();
            double posZ = mob.getZ();
            double motionX = mob.getDeltaMovement().x;
            double motionY = mob.getDeltaMovement().y;
            double motionZ = mob.getDeltaMovement().z;
            BlockPos dest = BlockPos.containing(mob.orbitOffset);

            double speedMod = 1.3;
            if (dest.getX() != 0 || dest.getY() != 0 || dest.getZ() != 0) {
                double targetX = dest.getX() + 0.5;
                double targetY = dest.getY() + 0.5;
                double targetZ = dest.getZ() + 0.5;
                Vec3 targetVector = new Vec3(targetX - posX, targetY - posY, targetZ - posZ);
                double length = targetVector.length();
                targetVector = targetVector.scale(0.3 / length);
                double weight = 0;
                if (length <= 3) {
                    weight = 0.9 * ((3.0 - length) / 3.0);
                }

                motionX = (0.9 - weight) * motionX + (speedMod + weight) * targetVector.x;
                motionY = (0.9 - weight) * motionY + (speedMod + weight) * targetVector.y;
                motionZ = (0.9 - weight) * motionZ + (speedMod + weight) * targetVector.z;
            }
            mob.setDeltaMovement(motionX, motionY, motionZ);
            faceBlock(BlockPos.containing(mob.orbitOffset), mob);
        }
    }

    public static void faceBlock(final BlockPos block, final LivingEntity citizen) {
        final double xDifference = block.getX() - citizen.blockPosition().getX();
        final double zDifference = block.getZ() - citizen.blockPosition().getZ();
        final double yDifference = block.getY() - (citizen.blockPosition().getY() + citizen.getEyeHeight());
        final double squareDifference = Math.sqrt(xDifference * xDifference + zDifference * zDifference);
        final double intendedRotationYaw = (Math.atan2(zDifference, xDifference) * 180.0D / Math.PI) - 90.0;
        final double intendedRotationPitch = -(Math.atan2(yDifference, squareDifference) * 180.0D / Math.PI);
        citizen.yRot = (float) (updateRotation(citizen.getYRot(), intendedRotationYaw, 360.0) % 360.0F);
        citizen.xRot = (float) (updateRotation(citizen.getXRot(), intendedRotationPitch, 360.0) % 360.0F);
    }

    public static double updateRotation(final double currentRotation, final double intendedRotation, final double maxIncrement) {
        double wrappedAngle = Mth.wrapDegrees(intendedRotation - currentRotation);
        if (wrappedAngle > maxIncrement) {
            wrappedAngle = maxIncrement;
        }
        if (wrappedAngle < -maxIncrement) {
            wrappedAngle = -maxIncrement;
        }
        return currentRotation + wrappedAngle;
    }

    @Override
    protected float getWaterSlowDown() {
        return isSwimming() ? super.getWaterSlowDown() : 0.8f;
    }

    @Override
    public boolean isAlive() {
        return !this.isRemoved();
    }

    public static AttributeSupplier.Builder getModdedAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 1000)
                .add(Attributes.MOVEMENT_SPEED, 0.33D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.6F)
                .add(Attributes.ATTACK_KNOCKBACK, 1.0D)
                .add(Attributes.ATTACK_DAMAGE, 8D)
                .add(Attributes.ARMOR, 6D)
                .add(Attributes.FOLLOW_RANGE, 100D)
                .add(Attributes.FLYING_SPEED, 0.4f)
                .add(Attributes.STEP_HEIGHT, 3.0f)
                .add(Attributes.ENTITY_INTERACTION_RANGE, 10.0f);
    }

    @Override
    public boolean isWithinMeleeAttackRange(LivingEntity pEntity) {
        return super.isWithinMeleeAttackRange(pEntity);
    }

    private static class ChimeraMusic extends AbstractTickableSoundInstance {
        private final WildenChimera chimera;

        private ChimeraMusic(WildenChimera chimera) {
            super(SoundRegistry.WILD_HUNT.get(), SoundSource.RECORDS, SoundInstance.createUnseededRandom());
            this.chimera = chimera;
            this.x = chimera.getX();
            this.y = chimera.getY();
            this.z = chimera.getZ();
            this.looping = true;
        }

        public static void play(WildenChimera chimera) {
            Minecraft.getInstance().getSoundManager().play(new ChimeraMusic(chimera));
        }

        @Override
        public void tick() {
            if (!chimera.isAlive()) {
                stop();
            }else{
                this.x = chimera.getX();
                this.y = chimera.getY();
                this.z = chimera.getZ();
            }
        }
    }
}
