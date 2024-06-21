package com.hollingsworth.arsnouveau.common.potions;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.event.BounceTimedEvent;
import com.hollingsworth.arsnouveau.api.event.EventQueue;
import com.hollingsworth.arsnouveau.setup.registry.ModPotions;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.entity.living.LivingHurtEvent;

@EventBusSubscriber(modid = ArsNouveau.MODID)
public class BounceEffect extends MobEffect {
    public BounceEffect() {
        super(MobEffectCategory.BENEFICIAL, 2039587);
    }

    // Adapted from Tinkers https://github.com/SlimeKnights/TinkersConstruct/blob/7df8a5dd62a3b731e59250c49300faadc24501d0/src/main/java/slimeknights/tconstruct/gadgets/GadgetEvents.java
    @SubscribeEvent
    public static void onFall(LivingFallEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity == null || !entity.hasEffect(ModPotions.BOUNCE_EFFECT.get())) {
            return;
        }
        boolean isPlayer = entity instanceof Player;
        boolean isClient = entity.level.isClientSide;
        if (isClient && !isPlayer) {
            return;
        }
        if (event.getDistance() > 2) {
            if (entity.isCrouching()) {
                event.setDamageMultiplier(0.0f);
            } else {
                event.setDamageMultiplier(0);
                entity.fallDistance = 0.0F;
                if (!isPlayer || isClient) {
                    double f = 0.95d - .1 * entity.getEffect(ModPotions.BOUNCE_EFFECT.get()).getAmplifier();
                    // only slow down half as much when bouncing
                    entity.setDeltaMovement(entity.getDeltaMovement().x / f, entity.getDeltaMovement().y * (-0.9), entity.getDeltaMovement().z / f);
                    entity.hurtMarked = true;
                    entity.setOnGround(false);
                }
                if (isClient) {
                    EventQueue.getClientQueue().addEvent(new BounceTimedEvent(entity, entity.getDeltaMovement().y));
                } else {
                    EventQueue.getServerInstance().addEvent(new BounceTimedEvent(entity, entity.getDeltaMovement().y));
                }
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onFlyWallDamage(LivingHurtEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity == null || !entity.hasEffect(ModPotions.BOUNCE_EFFECT.get())) {
            return;
        }
        boolean isPlayer = entity instanceof Player;
        if (!isPlayer) {
            return;
        }
        if(event.getSource().is(DamageTypes.FLY_INTO_WALL)){
            event.setAmount(0);
            Vec3 lookAngle = entity.getLookAngle();

            entity.setDeltaMovement(lookAngle.scale(-2));
            entity.hurtMarked = true;
        }
    }

}
