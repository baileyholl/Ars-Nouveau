package com.hollingsworth.arsnouveau.common.entity.familiar;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.scrying.CompoundScryer;
import com.hollingsworth.arsnouveau.api.scrying.TagScryer;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.ritual.RitualScrying;
import com.hollingsworth.arsnouveau.setup.registry.ModEntities;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
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

public class FamiliarStarbuncle extends FamiliarEntity {

    public FamiliarStarbuncle(EntityType<? extends PathfinderMob> ent, Level world) {
        super(ent, world);
        this.entityData.set(COLOR, DyeColor.ORANGE.getName());
    }

    @Override
    public void tick() {
        super.tick();
        if (!level.isClientSide && level.getGameTime() % 60 == 0 && getOwner() != null) {
            getOwner().addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 600, 1, false, false, true));
            this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 600, 1, false, false, true));
        }
    }

    @Override
    protected @NotNull InteractionResult mobInteract(@NotNull Player player, @NotNull InteractionHand hand) {
        if (!player.level.isClientSide && player.equals(getOwner())) {
            ItemStack stack = player.getItemInHand(hand);
            if (stack.is(Tags.Items.NUGGETS_GOLD)) {
                stack.shrink(1);
                RitualScrying.grantScrying((ServerPlayer) player, 3 * 20 * 60, new CompoundScryer(new TagScryer(Tags.Blocks.ORES_GOLD), new TagScryer(BlockTags.GOLD_ORES)));
                return InteractionResult.SUCCESS;
            }
            if (player.getMainHandItem().is(Tags.Items.DYES)) {
                DyeColor color = DyeColor.getColor(stack);
                if (color == null || this.entityData.get(COLOR).equals(color.getName()) || !Arrays.asList(Starbuncle.carbyColors).contains(color.getName()))
                    return InteractionResult.SUCCESS;
                setColor(color);
                return InteractionResult.SUCCESS;
            }
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
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder pBuilder) {
        super.defineSynchedData(pBuilder);
    }

    public ResourceLocation getTexture() {
        String color = getColor();
        if (color.isEmpty()) color = DyeColor.ORANGE.getName();

        return ArsNouveau.prefix( "textures/entity/starbuncle_" + color.toLowerCase() + ".png");
    }

    @Override
    public @NotNull EntityType<?> getType() {
        return ModEntities.ENTITY_FAMILIAR_STARBUNCLE.get();
    }

}
