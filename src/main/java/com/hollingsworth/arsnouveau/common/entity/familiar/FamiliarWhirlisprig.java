package com.hollingsworth.arsnouveau.common.entity.familiar;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.event.SpellCostCalcEvent;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.spell.SpellSchools;
import com.hollingsworth.arsnouveau.common.entity.Whirlisprig;
import com.hollingsworth.arsnouveau.setup.registry.ModEntities;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;


public class FamiliarWhirlisprig extends FlyingFamiliarEntity implements ISpellCastListener {
    public FamiliarWhirlisprig(EntityType<? extends PathfinderMob> ent, Level world) {
        super(ent, world);
    }


    @Override
    public InteractionResult interactAt(Player pPlayer, Vec3 pVec, InteractionHand hand) {
        if (hand != InteractionHand.MAIN_HAND || pPlayer.getCommandSenderWorld().isClientSide)
            return InteractionResult.PASS;

        ItemStack stack = pPlayer.getItemInHand(hand);
        String color = Whirlisprig.getColorFromStack(stack);
        if (color != null && !getColor().equals(color)) {
            setColor(color);
            stack.shrink(1);
            return InteractionResult.SUCCESS;
        }
        return super.interactAt(pPlayer, pVec, hand);
    }

    @Override
    public void onCostCalc(SpellCostCalcEvent event) {
        if (!isAlive())
            return;

        if (getOwner() != null && getOwner().equals(event.context.getUnwrappedCaster())) {
            int discount = 0;
            Spell spell = event.context.getSpell();
            for (AbstractSpellPart part : spell.recipe()) {
                if (SpellSchools.ELEMENTAL_EARTH.isPartOfSchool(part)) {
                    discount += part.getCastingCost() * .5;
                }
            }
            event.currentCost -= discount;
        }
    }

    public void eatEvent(LivingEntityUseItemEvent.Finish event) {
        if (!isAlive())
            return;

        if (!event.getEntity().level.isClientSide && getOwner() != null && getOwner().equals(event.getEntity())) {
            FoodProperties food = event.getItem().getItem().getFoodProperties(event.getItem(), getOwner());
            if (food != null) {
                float saturationModifier = food.saturation();
                int nutrition = food.nutrition();
                float satAmount = nutrition * saturationModifier * 2.0f;
                if (event.getEntity() instanceof Player) {
                    FoodData stats = ((Player) event.getEntity()).getFoodData();
                    stats.saturationLevel += satAmount * .4f;
                }
            }
        }
    }

    @Override
    public PlayState walkPredicate(AnimationState event) {
        if (event.isMoving()) {
            event.getController().setAnimation(RawAnimation.begin().thenPlay("fly"));
        } else {
            event.getController().setAnimation(RawAnimation.begin().thenPlay("idle"));
        }
        return PlayState.CONTINUE;
    }

    @Override
    public EntityType<?> getType() {
        return ModEntities.ENTITY_FAMILIAR_SYLPH.get();
    }

    public ResourceLocation getTexture() {
        return ArsNouveau.prefix( "textures/entity/whirlisprig_" + (getColor().isEmpty() ? "summer" : getColor().toLowerCase()) + ".png");
    }
}
