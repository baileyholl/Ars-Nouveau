package com.hollingsworth.arsnouveau.common.entity.familiar;

import com.hollingsworth.arsnouveau.common.entity.ModEntities;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;

public class FamiliarAmethystGolem extends FamiliarEntity{
    public FamiliarAmethystGolem(EntityType<? extends PathfinderMob> p_i48575_1_, Level p_i48575_2_) {
        super(p_i48575_1_, p_i48575_2_);
    }

    @Override
    public PlayState walkPredicate(AnimationEvent<?> animationEvent) {
        if (animationEvent.isMoving()) {
            animationEvent.getController().setAnimation(new AnimationBuilder().addAnimation("run"));
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    @Override
    public EntityType<?> getType() {
        return ModEntities.FAMILIAR_AMETHYST_GOLEM.get();
    }
}
