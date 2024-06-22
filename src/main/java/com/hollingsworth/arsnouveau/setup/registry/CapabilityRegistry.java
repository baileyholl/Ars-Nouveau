package com.hollingsworth.arsnouveau.setup.registry;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.mana.IManaCap;
import com.hollingsworth.arsnouveau.common.capability.*;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketSyncPlayerCap;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;

import java.util.List;

public class CapabilityRegistry {

    public static final EntityCapability<IManaCap, Void> MANA_CAPABILITY = EntityCapability.createVoid(ArsNouveau.prefix("mana"), IManaCap.class);
    public static final EntityCapability<IPlayerCap, Void> PLAYER_DATA_CAP = EntityCapability.createVoid(ArsNouveau.prefix("player_data"), IPlayerCap.class);

    /**
     * Get the {@link IManaCap} from the specified entity.
     *
     * @param entity The entity
     * @return A lazy optional containing the IMana, if any
     */
    public static LazyOptional<IManaCap> getMana(final LivingEntity entity) {
        if (entity == null)
            return LazyOptional.empty();
        return LazyOptional.of(entity.getCapability(MANA_CAPABILITY));
    }

    /**
     * Get the {@link IPlayerCap} from the specified entity.
     *
     * @param entity The entity
     * @return A lazy optional containing the IMana, if any
     */
    public static LazyOptional<IPlayerCap> getPlayerDataCap(final LivingEntity entity) {
        if (entity == null)
            return LazyOptional.empty();
        return LazyOptional.of(entity.getCapability(PLAYER_DATA_CAP));
    }

    /**
     * todo: DELETE
     * @deprecated
     */
    public static class LazyOptional<T> {
        private T val;
        private LazyOptional(T value) {
            this.val = value;
        }

        public static <T> LazyOptional<T> of(T value) {
            return new LazyOptional<>(value);
        }

        public T orElse(T value) {
            return val == null ? value : val;
        }

        public static <T> LazyOptional<T> empty() {
            return null;
        }
    }

    @SuppressWarnings("unused")
    @EventBusSubscriber(modid = ArsNouveau.MODID)
    public static class EventHandler {

        @SubscribeEvent
        public static void registerCapabilities(final RegisterCapabilitiesEvent event) {
            event.registerEntity(MANA_CAPABILITY, EntityType.PLAYER, (player, ctx) -> new ManaCap());
            event.registerEntity(PLAYER_DATA_CAP, EntityType.PLAYER, (player, ctx) -> new ANPlayerDataCap());
            var containers = List.of(BlockRegistry.ENCHANTING_APP_TILE, BlockRegistry.IMBUEMENT_TILE, BlockRegistry.SCRIBES_TABLE_TILE);
            for(var container : containers){
                event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, container.get(), (c, side) -> new InvWrapper(c));
            }
        }

        /**
         * Copy the player's mana when they respawn after dying or returning from the end.
         *
         * @param event The event
         */
        @SubscribeEvent
        public static void playerClone(PlayerEvent.Clone event) {
            Player oldPlayer = event.getOriginal();
//            oldPlayer.revive();
            var oldMana = getMana(oldPlayer).orElse(null);
            var newMana = getMana(event.getEntity()).orElse(null);
            if (oldMana != null && newMana != null) {
                newMana.setMaxMana(oldMana.getMaxMana());
                newMana.setMana(oldMana.getCurrentMana());
                newMana.setBookTier(oldMana.getBookTier());
                newMana.setGlyphBonus(oldMana.getGlyphBonus());
            }

            var oldPlayerCap = getPlayerDataCap(oldPlayer).orElse(null);
            var newPlayerCap = getPlayerDataCap(event.getEntity()).orElse(new ANPlayerDataCap());
            if (oldPlayerCap != null) {
                CompoundTag tag = oldPlayerCap.serializeNBT(event.getOriginal().level.registryAccess());
                newPlayerCap.deserializeNBT(event.getOriginal().level.registryAccess(), tag);
                syncPlayerCap(event.getEntity());
            }


//            event.getOriginal().invalidateCaps();
        }


        @SubscribeEvent
        public static void onPlayerLoginEvent(PlayerEvent.PlayerLoggedInEvent event) {
            if (event.getEntity() instanceof ServerPlayer) {
                syncPlayerCap(event.getEntity());
            }
        }

        @SubscribeEvent
        public static void respawnEvent(PlayerEvent.PlayerRespawnEvent event) {
            if (event.getEntity() instanceof ServerPlayer) {
                syncPlayerCap(event.getEntity());
            }
        }


        @SubscribeEvent
        public static void onPlayerStartTrackingEvent(PlayerEvent.StartTracking event) {
            if (event.getTarget() instanceof Player && event.getEntity() instanceof ServerPlayer) {
                syncPlayerCap(event.getEntity());
            }
        }

        @SubscribeEvent
        public static void onPlayerDimChangedEvent(PlayerEvent.PlayerChangedDimensionEvent event) {
            if (event.getEntity() instanceof ServerPlayer) {
                syncPlayerCap(event.getEntity());
            }
        }

        public static void syncPlayerCap(Player player) {
            IPlayerCap cap = CapabilityRegistry.getPlayerDataCap(player).orElse(new ANPlayerDataCap());
            CompoundTag tag = cap.serializeNBT(player.level.registryAccess());
            if(player instanceof ServerPlayer serverPlayer){
                Networking.sendToPlayerClient(new PacketSyncPlayerCap(tag), serverPlayer);
            }
        }
    }
}
