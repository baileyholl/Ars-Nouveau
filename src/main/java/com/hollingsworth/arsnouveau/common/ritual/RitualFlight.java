package com.hollingsworth.arsnouveau.common.ritual;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ritual.RangeEffectRitual;
import com.hollingsworth.arsnouveau.common.lib.RitualLib;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketUpdateFlight;
import com.hollingsworth.arsnouveau.setup.registry.ModPotions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.neoforge.event.entity.living.LivingEvent;

public class RitualFlight extends RangeEffectRitual {
    @Override
    public MobEffect getEffect() {
        return ModPotions.FLIGHT_EFFECT.get();
    }

    @Override
    public int getDuration() {
        return 60 * 20;
    }

    @Override
    public int getRange() {
        return 60;
    }

    @Override
    public int getSourceCost() {
        return 200;
    }

    @Override
    public ResourceLocation getRegistryName() {
        return ArsNouveau.prefix( RitualLib.FLIGHT);
    }

    @Override
    public String getLangDescription() {
        return "Grants nearby players the Flight effect when they jump, allowing them to creatively fly for a short time. If the player is nearby, this ritual will refresh their flight buff. Each time this ritual grants or refreshes flight, it will expend source from nearby jars.";
    }

    @Override
    public String getLangName() {
        return "Flight";
    }

    @Override
    public boolean applyEffect(ServerPlayer player) {
        boolean wasFlying = player.abilities.flying;
        boolean applied = super.applyEffect(player);
        if (applied) {
            player.abilities.mayfly = true;
            player.abilities.flying = wasFlying;
            Networking.sendToPlayerClient(new PacketUpdateFlight(true, wasFlying), player);
        }
        return applied;
    }

    public boolean onJumpEvent(LivingEvent.LivingJumpEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer && !serverPlayer.hasEffect(getEffect())) {
            return attemptRefresh(serverPlayer);
        }
        return false;
    }
}
