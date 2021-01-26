package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.common.block.tile.IAnimationListener;
import com.hollingsworth.arsnouveau.common.entity.goal.wilden.WildenMeleeAttack;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class  WildenHunter extends CreatureEntity implements IAnimatable, IAnimationListener {
    AnimationFactory manager = new AnimationFactory(this);
    protected WildenHunter(EntityType<? extends CreatureEntity> type, World worldIn) {
        super(type, worldIn);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

       // this.goalSelector.addGoal(4, new LeapAtTargetGoal(this, 0.4F));
        this.goalSelector.addGoal(5, new WildenMeleeAttack(this, 1.0D, true));
        this.goalSelector.addGoal(1, new SwimGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));

    }

    public static AttributeModifierMap.MutableAttribute getAttributes(){
        return MobEntity.func_233666_p_().createMutableAttribute(Attributes.MOVEMENT_SPEED, Attributes.MOVEMENT_SPEED.getDefaultValue())
                .createMutableAttribute(Attributes.MAX_HEALTH, 6.0D)
                .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.2D).createMutableAttribute(Attributes.ATTACK_DAMAGE, 3.5d);
    }

    @Override
    public void startAnimation(int arg) {
        try{
                        AnimationController controller = this.manager.getOrCreateAnimationData(this.hashCode()).getAnimationControllers().get("attackController");

//            if(controller.getCurrentAnimation() != null && controller.getCurrentAnimation().animationName.equals("attack")) {
//                System.out.println("cancalling");
//                return;
//            }
            controller.markNeedsReload();
            controller.setAnimation(new AnimationBuilder().addAnimation("attack"));
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("got arg" + arg);
        if(arg == Animations.ATTACK.ordinal()){
//            AnimationController controller = this.manager.getOrCreateAnimationData(this.hashCode()).getAnimationControllers().get("attackController");
//
//            if(controller.getCurrentAnimation() != null && controller.getCurrentAnimation().animationName.equals("attack")) {
//                System.out.println("cancalling");
//                return;
//            }
//            controller.markNeedsReload();
//            controller.setAnimation(new AnimationBuilder().addAnimation("attack"));
            System.out.println("playing anim");
        }
    }

    private <E extends Entity> PlayState attackPredicate(AnimationEvent event) {
        try{
            if (limbSwingAmount > 0.1) {
                //event.getController().setAnimation(new AnimationBuilder().addAnimation("running_legs", true));
                return PlayState.CONTINUE;
            }else if(this.isAggressive()){
                // event.getController().setAnimation(new AnimationBuilder().addAnimation("attack"));
                return PlayState.CONTINUE;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        //event.getController().setAnimation(new AnimationBuilder().addAnimation("idle", false));

        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController(this, "attackController", 1, this::attackPredicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return manager;
    }

    public enum Animations{
        ATTACK
    }
}
