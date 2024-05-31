package com.hollingsworth.arsnouveau.common.event;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.mana.IManaCap;
import com.hollingsworth.arsnouveau.api.util.ManaUtil;
import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketUpdateMana;
import com.hollingsworth.arsnouveau.setup.config.ServerConfig;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import var;

@EventBusSubscriber(modid = ArsNouveau.MODID)
public class ManaCapEvents {
    public static double MEAN_TPS = 20.0;

    @SubscribeEvent
    public static void playerOnTick(TickEvent.PlayerTickEvent e) {
        if (e.player.getCommandSenderWorld().isClientSide || e.player.getCommandSenderWorld().getGameTime() % ServerConfig.REGEN_INTERVAL.get() != 0)
            return;

        IManaCap mana = CapabilityRegistry.getMana(e.player).orElse(null);
        if (mana == null)
            return;
        // Force sync mana to client because client caps vanish on world change
        boolean shouldIgnoreMax = e.player.level().getGameTime() % 60 == 0;
        if (mana.getCurrentMana() != mana.getMaxMana() || shouldIgnoreMax) {
            double regenPerSecond = ManaUtil.getManaRegen(e.player) / Math.max(1, ((int) MEAN_TPS / ServerConfig.REGEN_INTERVAL.get()));
            mana.addMana(regenPerSecond);
            Networking.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) e.player), new PacketUpdateMana(mana.getCurrentMana(), mana.getMaxMana(), mana.getGlyphBonus(), mana.getBookTier()));
        }
        ManaUtil.Mana maxmana = ManaUtil.calcMaxMana(e.player);
        int max = maxmana.getRealMax();
        if (mana.getMaxMana() != max || shouldIgnoreMax) {
            mana.setMaxMana(max);
            Networking.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) e.player), new PacketUpdateMana(mana.getCurrentMana(), mana.getMaxMana(), mana.getGlyphBonus(), mana.getBookTier(), maxmana.Reserve()));
        }
    }

    @SubscribeEvent
    public static void playerRespawn(PlayerEvent.PlayerRespawnEvent e) {
        syncPlayerEvent(e.getEntity());
    }

    @SubscribeEvent
    public static void playerClone(PlayerEvent.Clone e) {
        if (e.getOriginal().level.isClientSide)
            return;

        CapabilityRegistry.getMana(e.getEntity()).ifPresent(newMana -> CapabilityRegistry.getMana(e.getOriginal()).ifPresent(origMana -> {
            newMana.setMaxMana(origMana.getMaxMana());
            newMana.setGlyphBonus(origMana.getGlyphBonus());
            newMana.setBookTier(origMana.getBookTier());
            Networking.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) e.getEntity()), new PacketUpdateMana(newMana.getCurrentMana(), newMana.getMaxMana(), newMana.getGlyphBonus(), newMana.getBookTier()));
        }));
    }

    @SubscribeEvent
    public static void playerLoggedIn(PlayerEvent.StartTracking e) {
        syncPlayerEvent(e.getEntity());
    }

    @SubscribeEvent
    public static void playerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent e) {
        syncPlayerEvent(e.getEntity());
    }

    public static void syncPlayerEvent(Player playerEntity) {
        if (playerEntity instanceof ServerPlayer) {
            CapabilityRegistry.getMana(playerEntity).ifPresent(mana -> {
                var manaCalc = ManaUtil.calcMaxMana(playerEntity);
                mana.setMaxMana(manaCalc.getRealMax());
                mana.setGlyphBonus(mana.getGlyphBonus());
                mana.setBookTier(mana.getBookTier());
                Networking.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) playerEntity), new PacketUpdateMana(mana.getCurrentMana(), mana.getMaxMana(), mana.getGlyphBonus(), mana.getBookTier(), manaCalc.Reserve()));
            });
        }
    }

    @SubscribeEvent
    public static void onTick(TickEvent.PlayerTickEvent e) {
        if (e.player.level.isClientSide)
            return;
        if (e.player.level.getGameTime() % 600 == 0 && e.player.getServer() != null) {

            double meanTickTime = mean(e.player.getServer().tickTimes) * 1.0E-6D;
            double meanTPS = Math.min(1000.0 / meanTickTime, 20);
            ManaCapEvents.MEAN_TPS = Math.max(1, meanTPS);
        }
    }

    private static long mean(long[] values) {
        long sum = 0L;
        for (long v : values)
            sum += v;
        return sum / values.length;
    }
}
