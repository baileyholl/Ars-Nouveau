package com.hollingsworth.arsnouveau.common.ritual;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ritual.RangeRitual;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.lib.RitualLib;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketUpdateFlight;
import com.hollingsworth.arsnouveau.setup.registry.ModPotions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingEvent;

public class RitualFlight extends RangeRitual {

    @Override
    public int getSourceCost() {
        return 200;
    }

    @Override
    public ResourceLocation getRegistryName() {
        return new ResourceLocation(ArsNouveau.MODID, RitualLib.FLIGHT);
    }

    @Override
    public String getLangDescription() {
        return "Grants nearby players the Flight effect when they jump, allowing them to creatively fly for a short time. If the player is nearby, this ritual will refresh their flight buff. Each time this ritual grants or refreshes flight, it will expend source from nearby jars.";
    }

    @Override
    public String getLangName() {
        return "Flight";
    }

    // Return true to stop checking all events
    public boolean refreshFlightEvent(ServerPlayer player) {
        if (!player.level.isClientSide
                && !needsSourceNow()
                && BlockUtil.distanceFrom(getPos(), player.blockPosition()) <= 60
                && player.abilities.flying) {
            player.addEffect(new MobEffectInstance(ModPotions.FLIGHT_EFFECT.get(), 60 * 20));
            player.abilities.mayfly = true;
            player.abilities.flying = true;
            Networking.sendToPlayerClient(new PacketUpdateFlight(true, true), player);
            setNeedsSource(true);
            return true;
        }
        return false;
    }

    public boolean onJumpEvent(LivingEvent.LivingJumpEvent event) {
        if (!needsSourceNow()
                && event.getEntity() instanceof Player entity
                && entity.getEffect(ModPotions.FLIGHT_EFFECT.get()) == null
                && BlockUtil.distanceFrom(getPos(), entity.blockPosition()) <= 60) {
            setNeedsSource(true);
            entity.addEffect(new MobEffectInstance(ModPotions.FLIGHT_EFFECT.get(), 90 * 20));
            return true;
        }
        return false;
    }
}
