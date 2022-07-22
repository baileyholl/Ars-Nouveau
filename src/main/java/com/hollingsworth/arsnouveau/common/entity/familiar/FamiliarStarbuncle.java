package com.hollingsworth.arsnouveau.common.entity.familiar;

import com.hollingsworth.arsnouveau.api.scrying.CompoundScryer;
import com.hollingsworth.arsnouveau.api.scrying.TagScryer;
import com.hollingsworth.arsnouveau.common.entity.ModEntities;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.ritual.RitualScrying;
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
import net.minecraftforge.common.Tags;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;

import java.util.Arrays;

public class FamiliarStarbuncle extends FamiliarEntity {

    public FamiliarStarbuncle(EntityType<? extends PathfinderMob> ent, Level world) {
        super(ent, world);
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
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
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

    public String getColor() {
        return this.entityData.get(COLOR);
    }


    @Override
    public PlayState walkPredicate(AnimationEvent event) {
        if (event.isMoving()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("run"));
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.set(COLOR, Starbuncle.COLORS.ORANGE.name());
    }

    @Override
    public EntityType<?> getType() {
        return ModEntities.ENTITY_FAMILIAR_STARBUNCLE.get();
    }

}
