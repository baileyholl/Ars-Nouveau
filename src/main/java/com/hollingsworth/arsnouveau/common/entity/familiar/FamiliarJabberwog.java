package com.hollingsworth.arsnouveau.common.entity.familiar;

import com.hollingsworth.arsnouveau.api.event.SpellModifierEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.PlayState;


public class FamiliarJabberwog extends FlyingFamiliarEntity {

    public FamiliarJabberwog(EntityType<? extends PathfinderMob> ent, Level world) {
        super(ent, world);

    }

    //@SubscribeEvent
    public void spellResolveEvent(SpellModifierEvent event) {
        if (isAlive() && getOwner() != null && getOwner().equals(event.caster)) {
            event.builder.addDamageModifier(3.0f);
        }
    }

    @Override
    public PlayState walkPredicate(AnimationState event) {
        if (event.isMoving()) {
            event.getController().setAnimation(RawAnimation.begin().thenPlay("hop"));
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }



    @Override
    public EntityType<?> getType() {
        return null;
//        return ModEntities.ENTITY_FAMILIAR_JABBERWOG.get();
    }
}
