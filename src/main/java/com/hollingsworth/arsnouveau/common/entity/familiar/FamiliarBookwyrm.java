package com.hollingsworth.arsnouveau.common.entity.familiar;

import com.hollingsworth.arsnouveau.api.event.SpellCastEvent;
import com.hollingsworth.arsnouveau.api.event.SpellModifierEvent;
import com.hollingsworth.arsnouveau.api.spell.SpellSchools;
import com.hollingsworth.arsnouveau.common.entity.ModEntities;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;

public class FamiliarBookwyrm extends FlyingFamiliarEntity implements ISpellCastListener{

    public FamiliarBookwyrm(EntityType<? extends CreatureEntity> ent, World world) {
        super(ent, world);
    }

    @Override
    public void onCast(SpellCastEvent event) {
        if(isAlive() && getOwner() != null && getOwner().equals(event.getEntity()))
            event.spell.setCost((int) (event.spell.getCastingCost() - event.spell.getCastingCost() * .15));
    }

    @Override
    public void onModifier(SpellModifierEvent event) {
        if(isAlive() && getOwner() != null && getOwner().equals(event.caster) && SpellSchools.ELEMENTAL.isPartOfSchool(event.spellPart)){
            event.builder.addDamageModifier(1.0f);
        }
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
