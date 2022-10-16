package com.hollingsworth.arsnouveau.common.event;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.mana.IManaCap;
import com.hollingsworth.arsnouveau.api.util.ManaUtil;
import com.hollingsworth.arsnouveau.common.capability.CapabilityRegistry;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketUpdateMana;
import com.hollingsworth.arsnouveau.setup.Config;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber(modid = ArsNouveau.MODID)
public class ManaCapEvents {
    public static double MEAN_TPS = 20.0;

    @SubscribeEvent
    public static void playerOnTick(TickEvent.PlayerTickEvent e) {
        if(e.player.getCommandSenderWorld().isClientSide || e.player.getCommandSenderWorld().getGameTime() % Config.REGEN_INTERVAL.get() != 0)
            return;

        IManaCap mana = CapabilityRegistry.getMana(e.player).orElse(null);
        if(mana == null)
            return;
        // Force sync mana to client because client caps vanish on world change
        boolean shouldIgnoreMax = e.player.getLevel().getGameTime() % 60 == 0;
        if (mana.getCurrentMana() != mana.getMaxMana() || shouldIgnoreMax) {
            double regenPerSecond = ManaUtil.getManaRegen(e.player) / Math.max(1, ((int)MEAN_TPS / Config.REGEN_INTERVAL.get()));
            mana.addMana(regenPerSecond);
            Networking.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) e.player), new PacketUpdateMana(mana.getCurrentMana(), mana.getMaxMana(), mana.getGlyphBonus(), mana.getBookTier()));
        }
        int max = ManaUtil.getMaxMana(e.player);
        if(mana.getMaxMana() != max || shouldIgnoreMax) {
            mana.setMaxMana(max);
            Networking.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) e.player), new PacketUpdateMana(mana.getCurrentMana(), mana.getMaxMana(), mana.getGlyphBonus(), mana.getBookTier()));
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

        CapabilityRegistry.getMana((LivingEntity) e.getEntity()).ifPresent(newMana -> CapabilityRegistry.getMana(e.getOriginal()).ifPresent(origMana -> {
            newMana.setMaxMana(origMana.getMaxMana());
            newMana.setGlyphBonus(origMana.getGlyphBonus());
            newMana.setBookTier(origMana.getBookTier());
            Networking.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer)  e.getEntity()), new PacketUpdateMana(newMana.getCurrentMana(), newMana.getMaxMana(), newMana.getGlyphBonus(), newMana.getBookTier()));
        }));
    }

    @SubscribeEvent
    public static void playerLoggedIn(PlayerEvent.StartTracking e) {
        syncPlayerEvent(e.getPlayer());
    }

    @SubscribeEvent
    public static void playerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent e) {
        syncPlayerEvent(e.getPlayer());
    }

    public static void syncPlayerEvent(Player playerEntity){
        if (playerEntity instanceof ServerPlayer) {
            CapabilityRegistry.getMana(playerEntity).ifPresent(mana -> {
                mana.setMaxMana(ManaUtil.getMaxMana(playerEntity));
                mana.setGlyphBonus(mana.getGlyphBonus());
                mana.setBookTier(mana.getBookTier());
                Networking.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) playerEntity), new PacketUpdateMana(mana.getCurrentMana(), mana.getMaxMana(), mana.getGlyphBonus(), mana.getBookTier()));
            });
        }
    }

    @SubscribeEvent
    public static void onTick(TickEvent.PlayerTickEvent e) {
        if(e.player.level.isClientSide)
            return;
        if(e.player.level.getGameTime() % 600 == 0 && e.player.getServer() != null) {

            double meanTickTime = mean(e.player.getServer().tickTimes) * 1.0E-6D;
            double meanTPS = Math.min(1000.0 / meanTickTime, 20);
            ManaCapEvents.MEAN_TPS = Math.max(1, meanTPS);
        }
    }
    private static long mean(long[] values)
    {
        long sum = 0L;
        for (long v : values)
            sum += v;
        return sum / values.length;
    }
}
