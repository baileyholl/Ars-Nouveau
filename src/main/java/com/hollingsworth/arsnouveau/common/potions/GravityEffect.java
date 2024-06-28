package com.hollingsworth.arsnouveau.common.potions;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.setup.registry.ModPotions;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingHurtEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = ArsNouveau.MODID)
public class GravityEffect extends MobEffect {

    public GravityEffect() {
        super(MobEffectCategory.HARMFUL, 2039587);
    }

    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int p_76394_2_) {
        if (!livingEntity.onGround()) {
            boolean isTooHigh = true;
            Level world = livingEntity.level;
            if (livingEntity instanceof Player) {
                for (int i = 1; i < 3; i++) {
                    if (!world.getBlockState(livingEntity.blockPosition().below(i)).isAir()) {
                        isTooHigh = false;
                        break;
                    }
                }
            }
            if (isTooHigh) {
                livingEntity.setDeltaMovement(livingEntity.getDeltaMovement().add(0, -0.5, 0));
                livingEntity.hurtMarked = true;
            }

        }

        return true;
    }

    // Disable flight here because items tick after our potions
    @SubscribeEvent
    public static void entityTick(PlayerTickEvent.Post e) {
        if ( e.getEntity().hasEffect(ModPotions.GRAVITY_EFFECT) && !e.getEntity().onGround() && !e.getEntity().isCreative()) {
            e.getEntity().abilities.flying = false;
        }
    }

    @SubscribeEvent
    public static void entityHurt(LivingHurtEvent e) {
        if (e.getSource().is(DamageTypes.FALL) && e.getEntity().hasEffect(ModPotions.GRAVITY_EFFECT)) {
            e.setAmount(e.getAmount() * 2.0f);
        }
    }
}
