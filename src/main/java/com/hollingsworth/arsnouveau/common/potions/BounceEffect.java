package com.hollingsworth.arsnouveau.common.potions;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.event.BounceTimedEvent;
import com.hollingsworth.arsnouveau.api.event.EventQueue;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ArsNouveau.MODID)
public class BounceEffect extends Effect {
    public BounceEffect() {
        super(EffectType.BENEFICIAL, 2039587);
        setRegistryName(ArsNouveau.MODID, "bounce");
    }
    // Adapted from Tinkers https://github.com/SlimeKnights/TinkersConstruct/blob/7df8a5dd62a3b731e59250c49300faadc24501d0/src/main/java/slimeknights/tconstruct/gadgets/GadgetEvents.java
    @SubscribeEvent
    public static void onFall(LivingFallEvent event) {
        LivingEntity entity = event.getEntityLiving();
        if (entity == null || !entity.hasEffect(ModPotions.BOUNCE_EFFECT)) {
            return;
        }
        boolean isPlayer = entity instanceof PlayerEntity;
        boolean isClient = entity.level.isClientSide;
        if (isClient && !isPlayer) {
            return;
        }
        if (event.getDistance() > 2) {
            if (entity.isCrouching()) {
                event.setDamageMultiplier(0.0f);
            } else {
                event.setDamageMultiplier(0);
                entity.fallDistance =  0.0F;
                if (!isPlayer || isClient) {
                    double f = 0.95d - .1 * entity.getEffect(ModPotions.BOUNCE_EFFECT).getAmplifier();
                    // only slow down half as much when bouncing
                    entity.setDeltaMovement(entity.getDeltaMovement().x /f , entity.getDeltaMovement().y * (-0.9), entity.getDeltaMovement().z/f );
                    entity.hurtMarked = true;
                    entity.setOnGround(false);
                }
                if(isClient){
                    EventQueue.getClientQueue().addEvent(new BounceTimedEvent(entity, entity.getDeltaMovement().y));
                }else{
                    EventQueue.getServerInstance().addEvent(new BounceTimedEvent(entity, entity.getDeltaMovement().y));
                }
                event.setCanceled(true);
            }
        }
    }

}
