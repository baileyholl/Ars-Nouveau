package com.hollingsworth.arsnouveau.setup.registry;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.item.inv.IMapInventory;
import com.hollingsworth.arsnouveau.api.mana.IManaCap;
import com.hollingsworth.arsnouveau.api.source.ISourceCap;
import com.hollingsworth.arsnouveau.common.block.tile.MobJarTile;
import com.hollingsworth.arsnouveau.common.capability.ANPlayerDataCap;
import com.hollingsworth.arsnouveau.common.capability.IPlayerCap;
import com.hollingsworth.arsnouveau.common.capability.ManaCap;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
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
import net.neoforged.neoforge.items.wrapper.InvWrapper;

import java.util.List;

public class CapabilityRegistry {

    public static final EntityCapability<ManaCap, Void> MANA_CAPABILITY = EntityCapability.createVoid(ArsNouveau.prefix("mana"), ManaCap.class);
    public static final EntityCapability<ANPlayerDataCap, Void> PLAYER_DATA_CAP = EntityCapability.createVoid(ArsNouveau.prefix("player_data"), ANPlayerDataCap.class);
    public static final BlockCapability<ISourceCap, Direction> SOURCE_CAPABILITY = BlockCapability.createSided(ArsNouveau.prefix("source"), ISourceCap.class);
    public static final BlockCapability<IMapInventory, Direction> MAP_INV_CAP = BlockCapability.createSided(ArsNouveau.prefix("map_inventory"), IMapInventory.class);

    /**
     * Get the {@link IManaCap} from the specified entity.
     *
     * @param entity The entity
     * @return A lazy optional containing the IMana, if any
     */
    public static ManaCap getMana(final LivingEntity entity) {
        if (entity == null)
            return null;
        return entity.getCapability(MANA_CAPABILITY);
    }

    /**
     * Get the {@link IPlayerCap} from the specified entity.
     *
     * @param entity The entity
     * @return A lazy optional containing the IMana, if any
     */
    public static ANPlayerDataCap getPlayerDataCap(final LivingEntity entity) {
        if (entity == null)
            return null;
        return entity.getCapability(PLAYER_DATA_CAP);
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerEntity(MANA_CAPABILITY, EntityType.PLAYER, (player, ctx) -> new ManaCap(player));
        event.registerEntity(PLAYER_DATA_CAP, EntityType.PLAYER, (player, ctx) -> new ANPlayerDataCap(player));
        var containers = List.of(BlockRegistry.ENCHANTING_APP_TILE,
                BlockRegistry.IMBUEMENT_TILE,
                BlockRegistry.SCRIBES_TABLE_TILE,
                BlockRegistry.ARCANE_PEDESTAL_TILE,
                BlockRegistry.ARCHWOOD_CHEST_TILE,
                BlockRegistry.REPOSITORY_TILE);
        for (var container : containers) {
            event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, container.get(), (c, side) -> new InvWrapper(c));
        }

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockRegistry.CRAFTING_LECTERN_TILE.get(), (c, side) -> c.getCapability(c, side));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockRegistry.REPOSITORY_CONTROLLER_TILE.get(), (c, side) -> c.getCapability(c, side));
        event.registerBlockEntity(MAP_INV_CAP, BlockRegistry.REPOSITORY_CONTROLLER_TILE.get(), (c, side) -> c);
        event.registerBlockEntity(MAP_INV_CAP, BlockRegistry.REPOSITORY_TILE.get(), (c, side) -> c);

        var sourceContainers = List.of(BlockRegistry.SOURCE_JAR_TILE, BlockRegistry.CREATIVE_SOURCE_JAR_TILE,
                BlockRegistry.AGRONOMIC_SOURCELINK_TILE, BlockRegistry.ALCHEMICAL_TILE, BlockRegistry.VITALIC_TILE, BlockRegistry.MYCELIAL_TILE, BlockRegistry.VOLCANIC_TILE,
                BlockRegistry.RELAY_COLLECTOR_TILE, BlockRegistry.RELAY_DEPOSIT_TILE, BlockRegistry.RELAY_WARP_TILE, BlockRegistry.ARCANE_RELAY_TILE,
                BlockRegistry.IMBUEMENT_TILE);

        for (var container : sourceContainers) {
            event.registerBlockEntity(SOURCE_CAPABILITY, container.get(), (sourceJar, side) -> sourceJar.getSourceStorage());
        }

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockRegistry.MOB_JAR_TILE.get(), (c, side) -> MobJarTile.SavingItemHandler.of(c, c.getEntityCapability(Capabilities.ItemHandler.ENTITY_AUTOMATION, null)));
    }


    @SuppressWarnings("unused")
    @EventBusSubscriber(modid = ArsNouveau.MODID)
    public static class EventHandler {

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
            if (player instanceof ServerPlayer serverPlayer) {
                ANPlayerDataCap playerData = getPlayerDataCap(serverPlayer);
                if (playerData != null) {
                    playerData.syncToClient(serverPlayer);
                }
            }
        }
    }
}
