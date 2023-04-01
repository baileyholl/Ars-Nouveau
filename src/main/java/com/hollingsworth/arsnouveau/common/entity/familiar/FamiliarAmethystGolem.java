package com.hollingsworth.arsnouveau.common.entity.familiar;

import com.hollingsworth.arsnouveau.common.entity.ModEntities;
import com.hollingsworth.arsnouveau.common.potions.ModPotions;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.Tags;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;

public class FamiliarAmethystGolem extends FamiliarEntity {
    public FamiliarAmethystGolem(EntityType<? extends PathfinderMob> p_i48575_1_, Level p_i48575_2_) {
        super(p_i48575_1_, p_i48575_2_);
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (level.isClientSide || hand != InteractionHand.MAIN_HAND)
            return InteractionResult.SUCCESS;

        if (player.getMainHandItem().is(Tags.Items.GEMS_AMETHYST)) {
            player.addEffect(new MobEffectInstance(ModPotions.DEFENCE_EFFECT.get(), 20 * 60 * 3));
            player.getMainHandItem().shrink(1);
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }
    @Override
    public PlayState walkPredicate(AnimationEvent<?> animationEvent) {
        if (animationEvent.isMoving()) {
            animationEvent.getController().setAnimation(new AnimationBuilder().addAnimation("run"));
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    @Override
    public EntityType<?> getType() {
        return ModEntities.FAMILIAR_AMETHYST_GOLEM.get();
    }
}
