package com.hollingsworth.arsnouveau.common.entity.familiar;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.client.IVariantTextureProvider;
import com.hollingsworth.arsnouveau.api.event.SpellCastEvent;
import com.hollingsworth.arsnouveau.api.event.SpellModifierEvent;
import com.hollingsworth.arsnouveau.api.spell.SpellSchools;
import com.hollingsworth.arsnouveau.common.entity.EntityBookwyrm;
import com.hollingsworth.arsnouveau.common.entity.ModEntities;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.Tags;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;

import java.util.Arrays;

public class FamiliarBookwyrm extends FlyingFamiliarEntity implements ISpellCastListener, IVariantTextureProvider<FamiliarBookwyrm> {

    public FamiliarBookwyrm(EntityType<? extends PathfinderMob> ent, Level world) {
        super(ent, world);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (level.isClientSide || hand != InteractionHand.MAIN_HAND)
            return InteractionResult.SUCCESS;

        ItemStack stack = player.getItemInHand(hand);

        if (player.getMainHandItem().is(Tags.Items.DYES)) {
            DyeColor color = DyeColor.getColor(stack);
            if (color == null || this.entityData.get(COLOR).equals(color.getName()) || !Arrays.asList(EntityBookwyrm.COLORS).contains(color.getName()))
                return InteractionResult.SUCCESS;
            setColor(color);
            return InteractionResult.SUCCESS;
        }

        return super.mobInteract(player, hand);

    }

    @Override
    public void onCast(SpellCastEvent event) {
        if (isAlive() && getOwner() != null && getOwner().equals(event.getEntity()))
            event.spell.addDiscount((int) (event.spell.getDiscountedCost() * .15));
    }

    @Override
    public void onModifier(SpellModifierEvent event) {
        if (isAlive() && getOwner() != null && getOwner().equals(event.caster) && SpellSchools.ELEMENTAL.isPartOfSchool(event.spellPart)) {
            event.builder.addDamageModifier(1.0f);
        }
    }

    @Override
    public PlayState walkPredicate(AnimationEvent<?> event) {
        event.getController().setAnimation(new AnimationBuilder().addAnimation("idle"));
        return PlayState.CONTINUE;
    }

    @Override
    public EntityType<?> getType() {
        return ModEntities.ENTITY_FAMILIAR_BOOKWYRM.get();
    }

    @Override
    public ResourceLocation getTexture(FamiliarBookwyrm entity) {
        String color = getEntityData().get(COLOR).toLowerCase();
        if (color.isEmpty())
            color = "blue";
        return new ResourceLocation(ArsNouveau.MODID, "textures/entity/book_wyrm_" + color + ".png");
    }
}
