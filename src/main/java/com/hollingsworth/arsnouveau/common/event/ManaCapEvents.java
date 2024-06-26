package com.hollingsworth.arsnouveau.common.event;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.mana.IManaCap;
import com.hollingsworth.arsnouveau.api.util.ManaUtil;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketUpdateMana;
import com.hollingsworth.arsnouveau.setup.config.ServerConfig;
import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;


@EventBusSubscriber(modid = ArsNouveau.MODID)
public class ManaCapEvents {
    public static double MEAN_TPS = 20.0;

    @SubscribeEvent
    public static void playerOnTick(PlayerTickEvent.Pre e) {
        Player player = e.getEntity();
        if (!(player instanceof ServerPlayer serverPlayer) || player.getCommandSenderWorld().getGameTime() % ServerConfig.REGEN_INTERVAL.get() != 0)
            return;

        IManaCap mana = CapabilityRegistry.getMana(player).orElse(null);
        if (mana == null)
            return;
        // Force sync mana to client because client caps vanish on world change
        boolean shouldIgnoreMax = player.level().getGameTime() % 60 == 0;
        if (mana.getCurrentMana() != mana.getMaxMana() || shouldIgnoreMax) {
            double regenPerSecond = ManaUtil.getManaRegen(player) / Math.max(1, ((int) MEAN_TPS / ServerConfig.REGEN_INTERVAL.get()));
            mana.addMana(regenPerSecond);
            Networking.sendToPlayerClient(new PacketUpdateMana(mana.getCurrentMana(), mana.getMaxMana(), mana.getGlyphBonus(), mana.getBookTier()), serverPlayer);
        }
        ManaUtil.Mana maxmana = ManaUtil.calcMaxMana(player);
        int max = maxmana.getRealMax();
        if (mana.getMaxMana() != max || shouldIgnoreMax) {
            mana.setMaxMana(max);
            Networking.sendToPlayerClient(new PacketUpdateMana(mana.getCurrentMana(), mana.getMaxMana(), mana.getGlyphBonus(), mana.getBookTier(), maxmana.Reserve()), serverPlayer);
        }
    }

    @SubscribeEvent
    public static void playerRespawn(PlayerEvent.PlayerRespawnEvent e) {
        syncPlayerEvent(e.getEntity());
    }

    @SubscribeEvent
    public static void playerClone(PlayerEvent.Clone e) {
        if (!(e.getOriginal() instanceof ServerPlayer serverPlayer))
            return;

        var newMana = CapabilityRegistry.getMana(e.getEntity()).orElse(null);
        var origMana = CapabilityRegistry.getMana(e.getOriginal()).orElse(null);
        if(newMana != null && origMana != null){
            newMana.setMaxMana(origMana.getMaxMana());
            newMana.setGlyphBonus(origMana.getGlyphBonus());
            newMana.setBookTier(origMana.getBookTier());
            Networking.sendToPlayerClient(new PacketUpdateMana(newMana.getCurrentMana(), newMana.getMaxMana(), newMana.getGlyphBonus(), newMana.getBookTier()), serverPlayer);
        }
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
        if (playerEntity instanceof ServerPlayer serverPlayer) {
            var mana = CapabilityRegistry.getMana(playerEntity).orElse(null);
            if (mana != null) {
                var manaCalc = ManaUtil.calcMaxMana(playerEntity);
                mana.setMaxMana(manaCalc.getRealMax());
                mana.setGlyphBonus(mana.getGlyphBonus());
                mana.setBookTier(mana.getBookTier());
                Networking.sendToPlayerClient(new PacketUpdateMana(mana.getCurrentMana(), mana.getMaxMana(), mana.getGlyphBonus(), mana.getBookTier(), manaCalc.Reserve()), serverPlayer);
            }
        }
    }

    @SubscribeEvent
    public static void onTick(PlayerTickEvent.Post e) {
        var player = e.getEntity();
        if (player.level.isClientSide)
            return;
        if (player.level.getGameTime() % 600 == 0 && player.getServer() != null) {

            double meanTickTime = mean(player.getServer().getTickTime(player.level.dimension())) * 1.0E-6D;
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
