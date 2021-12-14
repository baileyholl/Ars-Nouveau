package com.hollingsworth.arsnouveau.common.entity.familiar;

import com.hollingsworth.arsnouveau.api.event.SpellModifierEvent;
import com.hollingsworth.arsnouveau.api.spell.SpellSchools;
import com.hollingsworth.arsnouveau.common.entity.ModEntities;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LootingLevelEvent;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;

public class FamiliarDrygmy extends FamiliarEntity implements ISpellCastListener{

    public FamiliarDrygmy(EntityType<? extends PathfinderMob> ent, Level world) {
        super(ent, world);
    }


    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public void onModifier(SpellModifierEvent event) {
        if(isAlive() && getOwner() != null && getOwner().equals(event.caster) && SpellSchools.ELEMENTAL_EARTH.isPartOfSchool(event.spellPart)){
            event.builder.addDamageModifier(2.0f);
        }
    }

    public void onLootingEvent(LootingLevelEvent event){
        if (isAlive() && getOwner() != null && event.getDamageSource().getEntity() != null && getOwner().equals(event.getDamageSource().getEntity())) {
            if (level.random.nextFloat() > 0.4) {
                event.setLootingLevel(event.getLootingLevel() + 1 + random.nextInt(3));
            }
        }
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
