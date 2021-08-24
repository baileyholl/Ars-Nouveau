package com.hollingsworth.arsnouveau.common.entity.familiar;

import com.hollingsworth.arsnouveau.common.entity.ModEntities;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;

public class FamiliarWixie extends FlyingFamiliarEntity {

    public FamiliarWixie(EntityType<? extends CreatureEntity> ent, World world) {
        super(ent, world);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void treeGrow(PotionEvent.PotionAddedEvent event) {
        if(event.getEntity() != null && !event.getEntity().level.isClientSide && event.getEntity().equals(getOwner())){
            event.getPotionEffect().duration += event.getPotionEffect().duration * .2;
        }
    }

    @Override
    public void tick() {
        super.tick();
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
