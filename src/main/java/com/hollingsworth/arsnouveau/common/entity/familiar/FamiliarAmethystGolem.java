package com.hollingsworth.arsnouveau.common.entity.familiar;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.setup.registry.ModEntities;
import com.hollingsworth.arsnouveau.setup.registry.ModPotions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;

import java.util.HashMap;
import java.util.Map;

public class FamiliarAmethystGolem extends FamiliarEntity {
    public FamiliarAmethystGolem(EntityType<? extends PathfinderMob> p_i48575_1_, Level p_i48575_2_) {
        super(p_i48575_1_, p_i48575_2_);
    }

    @Override
    protected @NotNull InteractionResult mobInteract(@NotNull Player player, @NotNull InteractionHand hand) {
        if (level.isClientSide || hand != InteractionHand.MAIN_HAND)
            return InteractionResult.SUCCESS;

        if (player.getMainHandItem().is(Tags.Items.GEMS_AMETHYST)) {
            player.addEffect(new MobEffectInstance(ModPotions.DEFENCE_EFFECT, 20 * 60 * 3));
            if (!player.hasInfiniteMaterials()) {
                player.getMainHandItem().shrink(1);
            }
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
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
    public @NotNull EntityType<?> getType() {
        return ModEntities.FAMILIAR_AMETHYST_GOLEM.get();
    }

    public static final Map<String,ResourceLocation> Variants = new HashMap<>();
    static {
        Variants.put("default", ArsNouveau.prefix( "textures/entity/amethyst_golem.png"));
    }

    public ResourceLocation getTexture() {
        return Variants.getOrDefault(getColor(), Variants.get("default"));
    }

}
