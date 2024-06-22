package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.item.IWandable;
import com.hollingsworth.arsnouveau.api.spell.EntitySpellResolver;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.wrapped_caster.LivingCaster;
import com.hollingsworth.arsnouveau.api.util.NBTUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.tile.IAnimationListener;
import com.hollingsworth.arsnouveau.common.entity.goal.GoBackHomeGoal;
import com.hollingsworth.arsnouveau.common.entity.goal.wealdwalker.CastSpellGoal;
import com.hollingsworth.arsnouveau.common.entity.goal.wealdwalker.SmashGoal;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class WealdWalker extends AgeableMob implements GeoEntity, IAnimationListener, RangedAttackMob, IWandable, ITooltipProvider {

    public static final EntityDataAccessor<Boolean> SMASHING = SynchedEntityData.defineId(WealdWalker.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> CASTING = SynchedEntityData.defineId(WealdWalker.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> BABY = SynchedEntityData.defineId(WealdWalker.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Optional<BlockPos>> HOME = SynchedEntityData.defineId(WealdWalker.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
    public int smashCooldown;
    public int castCooldown;
    public Spell spell = new Spell();
    public ParticleColor color = ParticleColor.defaultParticleColor();

    public WealdWalker(EntityType<? extends AgeableMob> type, Level world) {
        super(type, world);
    }

    @Override
    public void tick() {
        super.tick();

        if (smashCooldown > 0)
            smashCooldown--;
        if (castCooldown > 0)
            castCooldown--;
        if (!level.isClientSide && level.getGameTime() % 20 == 0 && !this.isDeadOrDying()) {
            this.heal(1.0f);
        }
    }

    public InteractionResult mobInteract(Player p_230254_1_, InteractionHand p_230254_2_) {
        ItemStack itemstack = p_230254_1_.getItemInHand(p_230254_2_);
        if (itemstack.getItem() instanceof BoneMealItem && isBaby()) {
            int i = this.getAge();

            if (this.isBaby()) {
                this.usePlayerItem(p_230254_1_, itemstack);
                this.ageUp((int) ((float) (-i / 20) * 0.1F), true);
                return InteractionResult.sidedSuccess(this.level.isClientSide);
            }

            if (this.level.isClientSide) {
                return InteractionResult.CONSUME;
            }
        }

        return super.mobInteract(p_230254_1_, p_230254_2_);
    }

    protected void usePlayerItem(Player p_175505_1_, ItemStack p_175505_2_) {
        if (!p_175505_1_.getAbilities().instabuild) {
            p_175505_2_.shrink(1);
        }
    }

    @Override
    public void onFinishedConnectionFirst(@Nullable BlockPos storedPos, @Nullable LivingEntity storedEntity, Player playerEntity) {
        if (storedPos != null) {
            setHome(storedPos);
            PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.home_set"));
        }
    }

    @Override
    public EntityDimensions getDimensions(Pose p_213305_1_) {
        return isBaby() ? new EntityDimensions(1, 1, true) : super.getDimensions(p_213305_1_);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel p_241840_1_, AgeableMob p_241840_2_) {
        return null;
    }

    @Override
    public void die(DamageSource source) {
        if (!isBaby() && !level.isClientSide) {

            setBaby(true);
            refreshDimensions();
            this.setHealth(60);
            ParticleUtil.spawnPoof((ServerLevel) level, blockPosition().above());
            if (source.getEntity() != null && source.getEntity() instanceof Mob)
                ((Mob) source.getEntity()).setTarget(null);
            return;
        }
        super.die(source);
    }

    @Override
    public void setBaby(boolean baby) {
        super.setBaby(baby);
        this.entityData.set(BABY, baby);
    }

    @Override
    public void setAge(int age) {
        this.age = age;
        if (this.age >= 0 && !level.isClientSide) {
            this.ageBoundaryReached();
        }
    }

    public void setHome(BlockPos home) {
        this.entityData.set(HOME, Optional.of(home));
    }

    public @Nullable BlockPos getHome() {
        return this.entityData.get(HOME).orElse(null);
    }

    @Override
    protected void ageBoundaryReached() {
        super.ageBoundaryReached();
        if (!level.isClientSide)
            this.entityData.set(BABY, false);
    }

    @Override
    public boolean isBaby() {
        return this.entityData.get(BABY);
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> p_184206_1_) {
        if (BABY.equals(p_184206_1_)) {
            this.refreshDimensions();
        }

        super.onSyncedDataUpdated(p_184206_1_);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new GoBackHomeGoal(this, this::getHome, 10, () -> this.getTarget() == null || this.isBaby()));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Mob.class, false, (entity) -> {
            if (entity instanceof TamableAnimal tamableAnimal && tamableAnimal.isTame()) {
                return false;
            }
            return entity instanceof Enemy;
        }));
        this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(2, new SmashGoal(this, true, () -> smashCooldown <= 0 && !this.entityData.get(BABY), Animations.SMASH.ordinal(), 25, 5));
        this.goalSelector.addGoal(2, new CastSpellGoal(this, 1.2d,  15f, () -> castCooldown <= 0 && !this.entityData.get(BABY), Animations.CAST.ordinal(), 20));
    }

    @Override
    public boolean isAlliedTo(Entity pEntity) {
        return !(pEntity instanceof Enemy) || (pEntity instanceof TamableAnimal tamableAnimal && tamableAnimal.isTame()) || super.isAlliedTo(pEntity) ;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SMASHING, false);
        this.entityData.define(CASTING, false);
        this.entityData.define(BABY, false);
        this.entityData.define(HOME, Optional.empty());
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("isBaby", entityData.get(BABY));
        NBTUtil.storeBlockPos(tag, "home", getHome());
        tag.putInt("smash", smashCooldown);
        tag.putInt("cast", castCooldown);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypes.CACTUS) || source.is(DamageTypes.SWEET_BERRY_BUSH) || source.is(DamageTypes.DROWN))
            return false;
        return super.hurt(source, amount);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        entityData.set(BABY, tag.getBoolean("isBaby"));
        if (NBTUtil.hasBlockPos(tag, "home")) {
            setHome(NBTUtil.getBlockPos(tag, "home"));
        }
        this.smashCooldown = tag.getInt("smash");
        this.castCooldown = tag.getInt("cast");
    }

    AnimationController attackController;

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(new AnimationController<>(this, "run_controller", 1, this::runController));
        attackController = new AnimationController<>(this, "attack_controller", 5, this::attackController);
        data.add(attackController);
    }

    private PlayState attackController(software.bernie.geckolib.animation.AnimationState<?> AnimationState) {
        return PlayState.CONTINUE;
    }

    private PlayState runController(AnimationState AnimationState) {
        if (entityData.get(SMASHING) || entityData.get(CASTING))
            return PlayState.STOP;
        if (AnimationState.getController().getCurrentAnimation() != null && !(AnimationState.getController().getCurrentAnimation().animation().name().equals("run_master"))) {
            return PlayState.STOP;
        }
        if (AnimationState.isMoving()) {
            AnimationState.getController().setAnimation(RawAnimation.begin().thenPlay("run_master"));
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }

    @Override
    public void startAnimation(int arg) {
        try {
            if (arg == Animations.SMASH.ordinal()) {

                if (attackController.getCurrentAnimation() != null && (attackController.getCurrentAnimation().animation().name().equals("smash"))) {
                    return;
                }
                attackController.forceAnimationReset();
                attackController.setAnimation(RawAnimation.begin().thenPlay("smash").thenPlay("idle"));
            }

            if (arg == Animations.CAST.ordinal()) {
                if (attackController.getCurrentAnimation() != null && attackController.getCurrentAnimation().animation().name().equals("cast")) {
                    return;
                }
                attackController.forceAnimationReset();
                attackController.setAnimation(RawAnimation.begin().thenPlay("cast").thenPlay("idle"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static AttributeSupplier.Builder attributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 60d)
                .add(Attributes.MOVEMENT_SPEED, 0.2d)
                .add(Attributes.FOLLOW_RANGE, 16D)
                .add(Attributes.ATTACK_DAMAGE, 10.5d);
    }

    @Override
    public boolean removeWhenFarAway(double p_213397_1_) {
        return false;
    }

    @Override
    public int getBaseExperienceReward() {
        return 0;
    }

    @Override
    public void performRangedAttack(LivingEntity entity, float p_82196_2_) {
        EntitySpellResolver resolver = new EntitySpellResolver(new SpellContext(level, spell, this, new LivingCaster(this)).withColors(color));
        EntityProjectileSpell projectileSpell = new EntityProjectileSpell(level, resolver);
        projectileSpell.setColor(color);
        projectileSpell.shoot(this, this.getXRot(), this.getYRot(), 0.0F, 1.0f, 0.8f);
        level.addFreshEntity(projectileSpell);
        this.castCooldown = 40;
    }

    @Override
    public void getTooltip(List<Component> tooltip) {
        if (getHome() != null) {
            String home = getHome().getX() + ", " + getHome().getY() + ", " + getHome().getZ();
            tooltip.add(Component.translatable("ars_nouveau.weald_walker.home", home));
        } else {
            tooltip.add(Component.translatable("ars_nouveau.weald_walker.home", Component.translatable("ars_nouveau.nothing").getString()));
        }
    }

    public enum Animations {
        CAST,
        SMASH
    }
}
