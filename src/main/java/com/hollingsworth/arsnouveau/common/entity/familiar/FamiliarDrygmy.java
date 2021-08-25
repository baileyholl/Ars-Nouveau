package com.hollingsworth.arsnouveau.common.entity.familiar;

import com.hollingsworth.arsnouveau.api.event.SpellModifierEvent;
import com.hollingsworth.arsnouveau.api.spell.SpellSchools;
import com.hollingsworth.arsnouveau.common.entity.ModEntities;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LootingLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;

public class FamiliarDrygmy extends FamiliarEntity {

    public FamiliarDrygmy(EntityType<? extends CreatureEntity> ent, World world) {
        super(ent, world);
    }


    @SubscribeEvent
    public void spellResolveEvent(SpellModifierEvent event) {
        if(isAlive() && getOwner() != null && getOwner().equals(event.caster) && SpellSchools.ELEMENTAL_EARTH.isPartOfSchool(event.spellPart)){
            event.builder.addDamageModifier(2.0f);
        }
    }


    @SubscribeEvent
    public void fortuneEvent(LootingLevelEvent event) {
        if(isAlive() && getOwner() != null && event.getDamageSource().getEntity() != null &&  getOwner().equals(event.getDamageSource().getEntity())){
            if(level.random.nextFloat() > 0.4) {
                event.setLootingLevel(event.getLootingLevel() + 1 + random.nextInt(3));
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public PlayState walkPredicate(AnimationEvent event) {
        if (event.isMoving()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("run"));
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    @Override
    public EntityType<?> getType() {
        return ModEntities.ENTITY_FAMILIAR_DRYGMY;
    }
}
