package com.hollingsworth.arsnouveau.common.entity.familiar;

import com.hollingsworth.arsnouveau.common.entity.ModEntities;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;

public class FamiliarBookwyrm extends FlyingFamiliarEntity {

    public FamiliarBookwyrm(EntityType<? extends CreatureEntity> ent, World world) {
        super(ent, world);
    }

    @Override
    public void tick() {
        super.tick();

    }

    @Override
    public PlayState walkPredicate(AnimationEvent event) {
        event.getController().setAnimation(new AnimationBuilder().addAnimation("idle"));
        return PlayState.CONTINUE;
    }

    @Override
    public EntityType<?> getType() {
        return ModEntities.ENTITY_FAMILIAR_BOOKWYRM;
    }
}
