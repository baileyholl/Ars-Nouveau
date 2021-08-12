package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.common.entity.goal.FamiliarEntity;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;

public class FamiliarCarbuncle extends FamiliarEntity {

    public FamiliarCarbuncle(EntityType<? extends CreatureEntity> ent, World world) {
        super(ent, world);
    }

    @Override
    public PlayState walkPredicate(AnimationEvent event) {
        if(event.isMoving()){
            event.getController().setAnimation(new AnimationBuilder().addAnimation("hop"));
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    @Override
    public EntityType<?> getType() {
        return ModEntities.ENTITY_FAMILIAR_CARBUNCLE;
    }


}
