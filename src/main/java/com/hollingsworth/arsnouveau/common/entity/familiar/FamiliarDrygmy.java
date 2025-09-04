package com.hollingsworth.arsnouveau.common.entity.familiar;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.event.SpellModifierEvent;
import com.hollingsworth.arsnouveau.api.spell.SpellSchools;
import com.hollingsworth.arsnouveau.common.entity.EntityDrygmy;
import com.hollingsworth.arsnouveau.setup.registry.ModEntities;
import com.hollingsworth.arsnouveau.setup.registry.ModPotions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;

import java.util.Arrays;

public class FamiliarDrygmy extends FamiliarEntity implements ISpellCastListener {

    public FamiliarDrygmy(EntityType<? extends PathfinderMob> ent, Level world) {
        super(ent, world);
    }

    @Override
    protected @NotNull InteractionResult mobInteract(@NotNull Player player, @NotNull InteractionHand hand) {
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

    @Override
    public void applyTickEffects() {
        if (!level.isClientSide && level.getGameTime() % 60 == 0 && getOwner() != null) {
            getOwner().addEffect(new MobEffectInstance(ModPotions.LOOTING_EFFECT, 600, 0, false, false, true));
        }
    }

    @Override
    public PlayState walkPredicate(AnimationState<? extends FamiliarEntity> event) {
        if (event.isMoving()) {
            event.getController().setAnimation(RawAnimation.begin().thenPlay("run"));
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    @Override
    public @NotNull EntityType<?> getType() {
        return ModEntities.ENTITY_FAMILIAR_DRYGMY.get();
    }

    public ResourceLocation getTexture() {
        String color = getColor().toLowerCase();
        if (color.isEmpty()) color = "brown";
        return ArsNouveau.prefix("textures/entity/drygmy_" + color + ".png");
    }
}
