package com.hollingsworth.arsnouveau.common.entity.familiar;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.event.SpellModifierEvent;
import com.hollingsworth.arsnouveau.api.spell.SpellSchools;
import com.hollingsworth.arsnouveau.common.entity.EntityDrygmy;
import com.hollingsworth.arsnouveau.setup.registry.ModEntities;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.living.LootingLevelEvent;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.PlayState;

import java.util.Arrays;

public class FamiliarDrygmy extends FamiliarEntity implements ISpellCastListener {

    public FamiliarDrygmy(EntityType<? extends PathfinderMob> ent, Level world) {
        super(ent, world);
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (level.isClientSide || hand != InteractionHand.MAIN_HAND)
            return InteractionResult.SUCCESS;

        ItemStack stack = player.getItemInHand(hand);

        if (player.getMainHandItem().is(Tags.Items.DYES)) {
            DyeColor color = DyeColor.getColor(stack);
            if (color == null || this.entityData.get(COLOR).equals(color.getName()) || !Arrays.asList(EntityDrygmy.COLORS).contains(color.getName()))
                return InteractionResult.SUCCESS;
            setColor(color);
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public void onModifier(SpellModifierEvent event) {
        if (isAlive() && getOwner() != null && getOwner().equals(event.caster) && SpellSchools.ELEMENTAL_EARTH.isPartOfSchool(event.spellPart)) {
            event.builder.addDamageModifier(2.0f);
        }
    }

    public void onLootingEvent(LootingLevelEvent event) {
        if (event.getDamageSource() != null && isAlive() && getOwner() != null && event.getDamageSource().getEntity() != null && getOwner().equals(event.getDamageSource().getEntity())) {
            if (level.random.nextFloat() > 0.4) {
                event.setLootingLevel(event.getLootingLevel() + 1 + random.nextInt(3));
            }
        }
    }

    @Override
    public PlayState walkPredicate(AnimationState event) {
        if (event.isMoving()) {
            event.getController().setAnimation(RawAnimation.begin().thenPlay("run"));
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    @Override
    public EntityType<?> getType() {
        return ModEntities.ENTITY_FAMILIAR_DRYGMY.get();
    }

    @Override
    public ResourceLocation getTexture(FamiliarEntity entity) {
        String color = getColor().toLowerCase();
        if (color.isEmpty()) color = "brown";
        return ArsNouveau.prefix( "textures/entity/drygmy_" + color + ".png");
    }
}
