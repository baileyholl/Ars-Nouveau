package com.hollingsworth.arsnouveau.common.entity.familiar;

import com.hollingsworth.arsnouveau.api.event.SpellCastEvent;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.SpellSchools;
import com.hollingsworth.arsnouveau.common.entity.ModEntities;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Food;
import net.minecraft.util.FoodStats;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;

public class FamiliarSylph extends FlyingFamiliarEntity implements ISpellCastListener {

    public FamiliarSylph(EntityType<? extends CreatureEntity> ent, World world) {
        super(ent, world);
    }

    @Override
    public void onCast(SpellCastEvent event) {
        if(!isAlive())
            return;

        if(getOwner() != null && getOwner().equals(event.getEntity())) {
            int cost = event.spell.getCastingCost();
            for(AbstractSpellPart part : event.spell.recipe){
                if(part.getSchools().contains(SpellSchools.ELEMENTAL_EARTH)){
                    cost -= part.getConfigCost() * .5;
                }
            }
            event.spell.setCost(cost);
        }
    }

    public void eatEvent(LivingEntityUseItemEvent.Finish event) {
        if(!isAlive())
            return;

        if(!event.getEntityLiving().level.isClientSide && getOwner() != null && getOwner().equals(event.getEntity())) {
            if(event.getItem().getItem().getFoodProperties() != null && event.getItem().getItem().isEdible()){
                Food food = event.getItem().getItem().getFoodProperties();
                float saturationModifier = food.getSaturationModifier();
                int nutrition = food.getNutrition();
                float satAmount = nutrition * saturationModifier * 2.0f;
                if(event.getEntityLiving() instanceof PlayerEntity){
                    FoodStats stats = ((PlayerEntity) event.getEntityLiving()).getFoodData();
                    stats.saturationLevel += satAmount * .4f;
                }
            }
        }
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
        return ModEntities.ENTITY_FAMILIAR_SYLPH;
    }

}
