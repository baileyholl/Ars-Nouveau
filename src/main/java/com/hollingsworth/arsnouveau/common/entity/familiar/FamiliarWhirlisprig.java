package com.hollingsworth.arsnouveau.common.entity.familiar;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.client.IVariantTextureProvider;
import com.hollingsworth.arsnouveau.api.event.SpellCastEvent;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.SpellSchools;
import com.hollingsworth.arsnouveau.common.entity.ModEntities;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;

public class FamiliarWhirlisprig extends FlyingFamiliarEntity implements ISpellCastListener, IVariantTextureProvider {
    public FamiliarWhirlisprig(EntityType<? extends PathfinderMob> ent, Level world) {
        super(ent, world);
    }


    @Override
    public InteractionResult interactAt(Player pPlayer, Vec3 pVec, InteractionHand hand) {
        if(hand != InteractionHand.MAIN_HAND || pPlayer.getCommandSenderWorld().isClientSide )
            return InteractionResult.PASS;

        ItemStack stack = pPlayer.getItemInHand(hand);
        Item item = stack.getItem();
        if(Tags.Items.DYES.contains(item)){
            if(Tags.Items.DYES_GREEN.contains(item) && !getColor().equals("summer")){
                setColor("summer");
            }
            if(Tags.Items.DYES_ORANGE.contains(item) && !getColor().equals("autumn")){
                setColor("autumn");
            }
            if(Tags.Items.DYES_YELLOW.contains(item) && !getColor().equals("spring")){
                setColor("spring");
            }
            if(Tags.Items.DYES_WHITE.contains(item) && !getColor().equals("winter")){
                setColor("winter");
            }
            setColor(this.getEntityData().get(COLOR));
        }

        return super.interactAt(pPlayer, pVec, hand);
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
                FoodProperties food = event.getItem().getItem().getFoodProperties();
                float saturationModifier = food.getSaturationModifier();
                int nutrition = food.getNutrition();
                float satAmount = nutrition * saturationModifier * 2.0f;
                if(event.getEntityLiving() instanceof Player){
                    FoodData stats = ((Player) event.getEntityLiving()).getFoodData();
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

    @Override
    public ResourceLocation getTexture(LivingEntity entity) {
        return new ResourceLocation(ArsNouveau.MODID, "textures/entity/sylph_" +(getColor().isEmpty() ? "summer" : getColor()) + ".png");
    }
}
