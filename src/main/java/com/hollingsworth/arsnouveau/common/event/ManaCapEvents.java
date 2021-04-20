package com.hollingsworth.arsnouveau.common.event;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.mana.IMana;
import com.hollingsworth.arsnouveau.api.util.ManaUtil;
import com.hollingsworth.arsnouveau.common.capability.ManaCapability;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketUpdateMana;
import com.hollingsworth.arsnouveau.setup.Config;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;

@Mod.EventBusSubscriber(modid = ArsNouveau.MODID)
public class ManaCapEvents {

    @SubscribeEvent
    public static void playerOnTick(TickEvent.PlayerTickEvent e) {
        if(e.player.getCommandSenderWorld().isClientSide || e.player.getCommandSenderWorld().getGameTime() % Config.REGEN_INTERVAL.get() != 0)
            return;

        IMana mana = ManaCapability.getMana(e.player).orElse(null);
        if(mana == null)
            return;

        if (mana.getCurrentMana() != mana.getMaxMana()) {
            double regenPerSecond = ManaUtil.getManaRegen(e.player) / (20.0 / Config.REGEN_INTERVAL.get());
            mana.addMana(regenPerSecond);
            Networking.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) e.player), new PacketUpdateMana(mana.getCurrentMana(), mana.getMaxMana(), mana.getGlyphBonus(), mana.getBookTier()));
        }
        int max = ManaUtil.getMaxMana(e.player);
        if(mana.getMaxMana() != max) {
            mana.setMaxMana(max);
            Networking.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) e.player), new PacketUpdateMana(mana.getCurrentMana(), mana.getMaxMana(), mana.getGlyphBonus(), mana.getBookTier()));
        }
    }

    @SubscribeEvent
    public static void playerRespawn(PlayerEvent.PlayerRespawnEvent e) {
        syncPlayerEvent(e.getPlayer());
    }

    @SubscribeEvent
    public static void playerClone(PlayerEvent.Clone e) {
        if(e.getOriginal().level.isClientSide)
            return;

        ManaCapability.getMana((LivingEntity) e.getEntity()).ifPresent(newMana -> {
            ManaCapability.getMana(e.getOriginal()).ifPresent(origMana -> {
                newMana.setMaxMana(origMana.getMaxMana());
                newMana.setGlyphBonus(origMana.getGlyphBonus());
                newMana.setBookTier(origMana.getBookTier());
                Networking.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)  e.getEntity()), new PacketUpdateMana(newMana.getCurrentMana(), newMana.getMaxMana(), newMana.getGlyphBonus(), newMana.getBookTier()));
            });
        });
    }

    @SubscribeEvent
    public static void playerLoggedIn(PlayerEvent.StartTracking e) {
        syncPlayerEvent(e.getPlayer());
    }

    @SubscribeEvent
    public static void playerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent e) {
        syncPlayerEvent(e.getPlayer());
    }

    public static void syncPlayerEvent(PlayerEntity playerEntity){
        if (playerEntity instanceof ServerPlayerEntity) {
            ManaCapability.getMana(playerEntity).ifPresent(mana -> {
                mana.setMaxMana(ManaUtil.getMaxMana(playerEntity));
                mana.setGlyphBonus(mana.getGlyphBonus());
                Networking.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) playerEntity), new PacketUpdateMana(mana.getCurrentMana(), mana.getMaxMana(), mana.getGlyphBonus(), mana.getBookTier()));
            });
        }
    }
}
