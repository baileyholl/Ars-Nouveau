package com.hollingsworth.arsnouveau.common.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.world.BossInfo;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerBossInfo;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class WildenBoss extends MonsterEntity implements IAnimatable{
    private final ServerBossInfo bossEvent = (ServerBossInfo)(new ServerBossInfo(this.getDisplayName(), BossInfo.Color.PURPLE, BossInfo.Overlay.PROGRESS)).setDarkenScreen(true);

    protected WildenBoss(EntityType<? extends MonsterEntity> p_i48553_1_, World p_i48553_2_) {
        super(p_i48553_1_, p_i48553_2_);
    }

    @Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController<WildenBoss>(this, "walkController", 20, this::groundPredicate));
        animationData.addAnimationController(new AnimationController<WildenBoss>(this, "flyController", 20, this::flyPredicate));
    }

    private <E extends Entity> PlayState flyPredicate(AnimationEvent event) {
        return PlayState.STOP;
    }

    private<E extends Entity> PlayState groundPredicate(AnimationEvent e){
        if(e.isMoving()){

        }
        return PlayState.STOP;
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        this.bossEvent.setPercent(this.getHealth() / this.getMaxHealth());
    }

    protected boolean canRide(Entity p_184228_1_) {
        return false;
    }

    public void startSeenByPlayer(ServerPlayerEntity p_184178_1_) {
        super.startSeenByPlayer(p_184178_1_);
        this.bossEvent.addPlayer(p_184178_1_);
    }

    public void stopSeenByPlayer(ServerPlayerEntity p_184203_1_) {
        super.stopSeenByPlayer(p_184203_1_);
        this.bossEvent.removePlayer(p_184203_1_);
    }

    public boolean canChangeDimensions() {
        return false;
    }

    public boolean canBeAffected(EffectInstance p_70687_1_) {
        return super.canBeAffected(p_70687_1_);
    }

    AnimationFactory factory = new AnimationFactory(this);
    @Override
    public AnimationFactory getFactory() {
        return factory;
    }
}
