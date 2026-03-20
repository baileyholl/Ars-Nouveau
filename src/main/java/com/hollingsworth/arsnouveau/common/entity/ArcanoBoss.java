package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.common.entity.statemachine.arcano_boss.InitArcanoState;
import com.hollingsworth.nuggets.common.state_machine.SimpleStateMachine;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class ArcanoBoss extends Mob implements GeoEntity {
    private SimpleStateMachine stateMachine = new SimpleStateMachine(new InitArcanoState(this));

    public ArcanoBoss(EntityType<? extends Mob> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder attributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 600d)
                .add(Attributes.MOVEMENT_SPEED, 0.2d)
                .add(Attributes.FOLLOW_RANGE, 16D)
                .add(Attributes.ATTACK_DAMAGE, 10.5d);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.isDeadOrDying() && this.isEffectiveAi() && level instanceof ServerLevel serverLevel) {
            stateMachine.tick();
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }

    private AnimatableInstanceCache animatableInstanceCache = GeckoLibUtil.createInstanceCache(this);

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.animatableInstanceCache;
    }

}
