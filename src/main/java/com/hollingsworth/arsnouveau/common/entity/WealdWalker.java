package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.common.block.tile.IAnimationListener;
import com.hollingsworth.arsnouveau.common.entity.goal.wealdwalker.SmashGoal;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nullable;

public class WealdWalker extends AgeableEntity implements IAnimatable, IAnimationListener {

    BlockPos homePos;

    protected WealdWalker(EntityType<? extends AgeableEntity> type, World world) {
        super(type, world);
    }


    @Nullable
    @Override
    public AgeableEntity getBreedOffspring(ServerWorld p_241840_1_, AgeableEntity p_241840_2_) {
        return null;
    }

    @Override
    public void die(DamageSource p_70645_1_) {
        super.die(p_70645_1_);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new SwimGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, MonsterEntity.class, false));
        this.goalSelector.addGoal(8, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
        this.goalSelector.addGoal(8, new LookAtGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
        this.goalSelector.addGoal(2, new SmashGoal(this, 1.0, true));
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT tag) {
        super.addAdditionalSaveData(tag);
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT tag) {
        super.readAdditionalSaveData(tag);
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController(this,"run_controller", 1.0f, this::runController));
        data.addAnimationController(new AnimationController(this,"attack_controller", 1.0f, this::attackController));
    }
    private PlayState attackController(AnimationEvent animationEvent) {
        return PlayState.CONTINUE;
    }
    private PlayState runController(AnimationEvent animationEvent) {
        if(animationEvent.getController().getCurrentAnimation() != null && !(animationEvent.getController().getCurrentAnimation().animationName.equals("run_master"))) {
            System.out.println(animationEvent.getController().getCurrentAnimation().animationName);
            return PlayState.STOP;
        }
        if(animationEvent.isMoving()){
            animationEvent.getController().setAnimation(new AnimationBuilder().addAnimation("run_master"));
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    AnimationFactory factory = new AnimationFactory(this);
    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    @Override
    public void startAnimation(int arg) {
        try{
            if(arg == Animations.SMASH.ordinal()){
                AnimationController controller = this.factory.getOrCreateAnimationData(this.hashCode()).getAnimationControllers().get("attack_controller");

                if(controller.getCurrentAnimation() != null && (controller.getCurrentAnimation().animationName.equals("smash"))) {
                    return;
                }
                System.out.println("smash");
                controller.markNeedsReload();
                controller.setAnimation(new AnimationBuilder().addAnimation("smash").addAnimation("idle"));
            }

            if(arg == Animations.CAST.ordinal()){
                AnimationController controller = this.factory.getOrCreateAnimationData(this.hashCode()).getAnimationControllers().get("attack_controller");
                if(controller.getCurrentAnimation() != null && controller.getCurrentAnimation().animationName.equals("cast")) {
                    return;
                }
                controller.markNeedsReload();
                controller.setAnimation(new AnimationBuilder().addAnimation("cast"));
            }


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static AttributeModifierMap.MutableAttribute attributes() {
        return MobEntity.createMobAttributes().add(Attributes.MAX_HEALTH, 40d)
                .add(Attributes.MOVEMENT_SPEED, 0.2d).add(Attributes.FLYING_SPEED, Attributes.FLYING_SPEED.getDefaultValue())
                .add(Attributes.FOLLOW_RANGE, 16D).add(Attributes.ATTACK_DAMAGE, 6.0d);
    }

    public enum Animations{
        CAST,
        SMASH
    }
}
