package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.common.block.tile.IAnimationListener;
import com.hollingsworth.arsnouveau.common.entity.goal.wilden.WildenMeleeAttack;
import com.hollingsworth.arsnouveau.common.entity.goal.wilden.WildenRamAttack;
import com.hollingsworth.arsnouveau.common.entity.goal.wilden.WildenSummon;
import com.hollingsworth.arsnouveau.setup.Config;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class  WildenHunter extends Monster implements IAnimatable, IAnimationListener {
    AnimationFactory manager = new AnimationFactory(this);

    public WildenHunter(EntityType<? extends Monster> type, Level worldIn) {
        super(type, worldIn);
    }

    public WildenHunter(Level worldIn) {
        this(ModEntities.WILDEN_HUNTER, worldIn);
    }

    public int ramCooldown = 0;
    public int summonCooldown = 0;
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(5, new WildenMeleeAttack(this, 1.3D, true, WildenHunter.Animations.ATTACK.ordinal(), () -> true));
        this.goalSelector.addGoal(3, new WildenRamAttack(this, 2D, true));
        this.goalSelector.addGoal(3, new WildenSummon(this));
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        if(Config.HUNTER_ATTACK_ANIMALS.get())
            this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Animal.class, 10, true, false, (entity) -> !(entity instanceof SummonWolf) || !((SummonWolf) entity).isWildenSummon));

    }
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.WOLF_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.WOLF_DEATH;
    }

    @Override
    protected int getExperienceReward(Player player) {
        return 5;
    }

    /**
     * Returns the volume for the sounds this mob makes.
     */
    protected float getSoundVolume() {
        return 0.4F;
    }

    @Override
    public void playSound(SoundEvent soundIn, float volume, float pitch) {
        super.playSound(soundIn, volume, pitch -0.5f);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.WOLF_GROWL;
    }

    public static AttributeSupplier.Builder getModdedAttributes(){
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.6F)
                .add(Attributes.ATTACK_KNOCKBACK, 1.0D)
                .add(Attributes.ATTACK_DAMAGE, 4.5D)
                .add(Attributes.ARMOR, 2.0D);
    }

    @Override
    public void tick() {
        super.tick();
        if(level.isClientSide)
            return;
        if(ramCooldown > 0)
            ramCooldown--;
        if(summonCooldown > 0)
            summonCooldown--;
    }

    @Override
    public void startAnimation(int arg) {
        try{
            if(controller == null)
                return;
            if(arg == Animations.ATTACK.ordinal()){
                if(controller.getCurrentAnimation() != null && (controller.getCurrentAnimation().animationName.equals("attack") || controller.getCurrentAnimation().animationName.equals("attack2") ||
                        controller.getCurrentAnimation().animationName.equals("howl"))) {
                    return;
                }
                controller.markNeedsReload();
                controller.setAnimation(new AnimationBuilder().addAnimation("attack").addAnimation("idle"));
            }

            if(arg == Animations.RAM.ordinal()){
                if(controller.getCurrentAnimation() != null && controller.getCurrentAnimation().animationName.equals("attack2")) {
                    return;
                }
                controller.markNeedsReload();
                controller.setAnimation(new AnimationBuilder().addAnimation("attack2").addAnimation("idle"));
            }

            if(arg == Animations.HOWL.ordinal()){
                controller.markNeedsReload();
                controller.setAnimation(new AnimationBuilder().addAnimation("howl").addAnimation("idle"));
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private <E extends Entity> PlayState attackPredicate(AnimationEvent event) {
        return PlayState.CONTINUE;
    }

    AnimationController controller;
    @Override
    public void registerControllers(AnimationData animationData) {
        controller = new AnimationController(this, "attackController", 1, this::attackPredicate);
        animationData.addAnimationController(controller);
    }

    @Override
    public AnimationFactory getFactory() {
        return manager;
    }

    public enum Animations{
        ATTACK,
        RAM,
        HOWL
    }
}
