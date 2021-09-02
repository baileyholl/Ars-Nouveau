package com.hollingsworth.arsnouveau.common.entity.familiar;

import com.hollingsworth.arsnouveau.common.entity.ModEntities;
import com.hollingsworth.arsnouveau.common.ritual.ScryingRitual;
import net.minecraft.block.Blocks;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.common.Tags;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;

public class FamiliarCarbuncle extends FamiliarEntity {

    public FamiliarCarbuncle(EntityType<? extends CreatureEntity> ent, World world) {
        super(ent, world);
    }

    @Override
    public void tick() {
        super.tick();
        if(!level.isClientSide && level.getGameTime() % 60 == 0 && getOwner() != null){
            getOwner().addEffect(new EffectInstance(Effects.MOVEMENT_SPEED, 600, 1, false, false, true));
            this.addEffect(new EffectInstance(Effects.MOVEMENT_SPEED, 600, 1, false, false, true));
        }
    }

    @Override
    protected ActionResultType mobInteract(PlayerEntity player, Hand hand) {
        if(!player.level.isClientSide && player.equals(getOwner())){
            ItemStack stack = player.getItemInHand(hand);
            if(stack.getItem().is(Tags.Items.NUGGETS_GOLD)){
                stack.shrink(1);
                ScryingRitual.grantScrying((ServerPlayerEntity) player, new ItemStack(Blocks.GOLD_ORE), 3 * 20 * 60);
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
