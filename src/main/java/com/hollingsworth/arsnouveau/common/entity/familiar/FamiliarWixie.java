package com.hollingsworth.arsnouveau.common.entity.familiar;

import com.hollingsworth.arsnouveau.common.entity.ModEntities;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;

public class FamiliarWixie extends FlyingFamiliarEntity {

    public FamiliarWixie(EntityType<? extends CreatureEntity> ent, World world) {
        super(ent, world);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void tick() {
        super.tick();
        if(!level.isClientSide && level.getGameTime() % 60 == 0 && getOwner() != null){
            getOwner().addEffect(new EffectInstance(Effects.MOVEMENT_SPEED, 600, 1, false, false, true));
            this.addEffect(new EffectInstance(Effects.MOVEMENT_SPEED, 600, 1, false, false, true));
        }
    }

    @Override
    public PlayState walkPredicate(AnimationEvent event) {
        if(getNavigation().isInProgress())
            return PlayState.STOP;
        event.getController().setAnimation(new AnimationBuilder().addAnimation("idle"));
        return PlayState.CONTINUE;
    }

    @Override
    public EntityType<?> getType() {
        return ModEntities.ENTITY_FAMILIAR_WIXIE;
    }
}
