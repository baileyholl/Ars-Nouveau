package com.hollingsworth.arsnouveau.common.entity.familiar;

import com.hollingsworth.arsnouveau.common.entity.ModEntities;
import com.hollingsworth.arsnouveau.common.ritual.ScryingRitual;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.Tags;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;

public class FamiliarCarbuncle extends FamiliarEntity {

    public FamiliarCarbuncle(EntityType<? extends PathfinderMob> ent, Level world) {
        super(ent, world);
    }

    @Override
    public void tick() {
        super.tick();
        if(!level.isClientSide && level.getGameTime() % 60 == 0 && getOwner() != null){
            getOwner().addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 600, 1, false, false, true));
            this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 600, 1, false, false, true));
        }
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if(!player.level.isClientSide && player.equals(getOwner())){
            ItemStack stack = player.getItemInHand(hand);
            if(stack.getItem().is(Tags.Items.NUGGETS_GOLD)){
                stack.shrink(1);
                ScryingRitual.grantScrying((ServerPlayer) player, new ItemStack(Blocks.GOLD_ORE), 3 * 20 * 60);
            }
        }
        return super.mobInteract(player, hand);
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
    public EntityType<?> getType() {
        return ModEntities.ENTITY_FAMILIAR_CARBUNCLE;
    }
}
