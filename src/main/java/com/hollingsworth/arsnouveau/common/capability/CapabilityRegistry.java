package com.hollingsworth.arsnouveau.common.capability;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.mana.IManaCap;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketSyncFamiliars;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class CapabilityRegistry {

    public static final Capability<IManaCap> MANA_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});
    public static final Capability<IPlayerCap> PLAYER_DATA_CAP = CapabilityManager.get(new CapabilityToken<>(){});


    public static final Direction DEFAULT_FACING = null;


    /**
     * Get the {@link IManaCap} from the specified entity.
     *
     * @param entity The entity
     * @return A lazy optional containing the IMana, if any
     */
    public static LazyOptional<IManaCap> getMana(final LivingEntity entity){
        return entity.getCapability(MANA_CAPABILITY, DEFAULT_FACING);
    }

    /**
     * Get the {@link IPlayerCap} from the specified entity.
     *
     * @param entity The entity
     * @return A lazy optional containing the IMana, if any
     */
    public static LazyOptional<IPlayerCap> getPlayerDataCap(final LivingEntity entity){
        return entity.getCapability(PLAYER_DATA_CAP, DEFAULT_FACING);
    }
    /**
     * Event handler for the {@link IManaCap} capability.
     */
    @SuppressWarnings("unused")
    @Mod.EventBusSubscriber(modid = ArsNouveau.MODID)
    public static class EventHandler {

        /**
         * Attach the {@link IManaCap} capability to all living entities.
         *
         * @param event The event
         */
        @SubscribeEvent
        public static void attachCapabilities(final AttachCapabilitiesEvent<Entity> event) {
            ManaCapAttacher.attach(event);
            ANPlayerCapAttacher.attach(event);
        }

        @SubscribeEvent
        public static void registerCapabilities(final RegisterCapabilitiesEvent event) {
            event.register(IManaCap.class);
            event.register(IPlayerCap.class);
        }
        /**
         * Copy the player's mana when they respawn after dying or returning from the end.
         *
         * @param event The event
         */
        @SubscribeEvent
        public static void playerClone(final PlayerEvent.Clone event) {
            getMana(event.getOriginal()).ifPresent(oldMaxMana -> getMana(event.getPlayer()).ifPresent(newMaxMana -> {
                newMaxMana.setMaxMana(oldMaxMana.getMaxMana());
                newMaxMana.setMana(oldMaxMana.getCurrentMana());
                newMaxMana.setBookTier(oldMaxMana.getBookTier());
                newMaxMana.setGlyphBonus(oldMaxMana.getGlyphBonus());
            }));

            getPlayerDataCap(event.getOriginal()).ifPresent(oldFamiliarCap -> getPlayerDataCap(event.getPlayer()).ifPresent(newFamiliarCap -> {
                newFamiliarCap.setUnlockedFamiliars(oldFamiliarCap.getUnlockedFamiliars());
                syncFamiliars(event.getPlayer());
            }));
        }


        @SubscribeEvent
        public static void onPlayerLoginEvent(PlayerEvent.PlayerLoggedInEvent event) {
            if(event.getPlayer() instanceof ServerPlayer){
                syncFamiliars(event.getPlayer());
            }
        }

        @SubscribeEvent
        public static void respawnEvent(PlayerEvent.PlayerRespawnEvent event) {
            if(event.getPlayer() instanceof ServerPlayer)
               syncFamiliars(event.getPlayer());
        }


        @SubscribeEvent
        public static void onPlayerStartTrackingEvent(PlayerEvent.StartTracking event) {
            if (event.getTarget() instanceof Player && event.getPlayer() instanceof ServerPlayer) {
                syncFamiliars(event.getPlayer());
            }
        }

        @SubscribeEvent
        public static void onPlayerDimChangedEvent(PlayerEvent.PlayerChangedDimensionEvent event) {
            if (event.getPlayer() instanceof ServerPlayer)
                syncFamiliars(event.getPlayer());
        }

        public static void syncFamiliars(Player player){
            IPlayerCap cap = CapabilityRegistry.getPlayerDataCap(player).orElse(new ANPlayerDataCap());
            CompoundTag tag = new CompoundTag();
            cap.deserializeNBT(tag);
            Networking.sendToPlayer(new PacketSyncFamiliars(tag), player);
        }
    }
}
