package com.hollingsworth.arsnouveau.common.potions;

import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketUpdateFlight;
import com.hollingsworth.arsnouveau.setup.registry.ModPotions;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.player.Player;

public class FlightEffect extends MobEffect {

    public FlightEffect() {
        super(MobEffectCategory.BENEFICIAL, 2039587);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int p_76394_2_) {
        if (entity instanceof Player player) {
            player.abilities.mayfly = player.isCreative() || entity.isSpectator() || getFlightDuration(entity) > 2;
        }
        return true;
    }

    @Override
    public void removeAttributeModifiers(AttributeMap pAttributeMap) {
        super.removeAttributeModifiers(pAttributeMap);
        if (entity instanceof Player player) {
            // check for effect duration because this is also called from LivingEntity::onEffectUpdated
            boolean canFly = player.isCreative() || entity.isSpectator() || getFlightDuration(entity) > 2;
            boolean wasFlying = canFly && player.abilities.flying;
            player.abilities.mayfly = canFly;
            player.abilities.flying = wasFlying;
            Networking.sendToPlayerClient(new PacketUpdateFlight(canFly, wasFlying), (ServerPlayer) player);
        }
    }

    public int getFlightDuration(LivingEntity entity) {
        MobEffectInstance effect = entity.getEffect(ModPotions.FLIGHT_EFFECT);
        return effect != null ? effect.getDuration() : 0;
    }
}
