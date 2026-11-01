package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.client.renderer.PlanariumRenderingWorld;
import com.hollingsworth.arsnouveau.client.renderer.world.CulledStatePos;
import com.hollingsworth.arsnouveau.common.block.DimBoundary;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.common.mixin.structure.StructureTemplateAccessor;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketClientRequestDim;
import com.hollingsworth.arsnouveau.common.network.PacketUpdateDimTile;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectName;
import com.hollingsworth.arsnouveau.common.world.dimension.PlanariumChunkGenerator;
import com.hollingsworth.arsnouveau.common.world.saved_data.DimMappingData;
import com.hollingsworth.arsnouveau.common.world.saved_data.JarDimData;
import com.hollingsworth.arsnouveau.setup.config.ServerConfig;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.hollingsworth.nuggets.common.util.BlockPosHelpers;
import com.hollingsworth.nuggets.common.util.WorldHelpers;
import net.commoble.infiniverse.internal.DimensionManager;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Tuple;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.event.level.BlockEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class PlanariumTile extends PlanariumRenderingTile {

    // TODO breaking change: move to PlanariumRenderingTile
    public static DimManager dimManager = new DimManager();
    public static Map<ResourceKey<Level>, ClientDimEntry> clientTemplates = new ConcurrentHashMap<>();

    public PlanariumTile(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
    }

    public PlanariumTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.PLANARIUM_TILE.get(), pos, state);
    }


    public ServerLevel setDimension(String dimName, ServerLevel serverLevel) {
        Tuple<ServerLevel, DimManager.Entry> dimension = dimManager.getOrCreateLevel(dimName, serverLevel, worldPosition);
        DimMappingData.Entry dimData = DimMappingData.from(serverLevel).getOrCreateByName(dimName);
        this.key = ResourceKey.create(Registries.DIMENSION, dimData.key());
        this.name = Component.literal(dimName);
        var managerEntry = dimension.getB();
        if (managerEntry != null) {
            lastUpdated = managerEntry.lastUpdated;
            Networking.sendToNearbyClient(level, worldPosition, new PacketUpdateDimTile(worldPosition, managerEntry.template));
            updateBlock();
        }
        return dimension.getA();
    }

    public void sendEntityTo(Entity entity) {
        if (key != null && level instanceof ServerLevel serverLevel) {
            ServerLevel dimLevel = level.getServer().getLevel(key);
            if (dimLevel == null) {
                return;
            }
            // Ensure we are not already in another jar dimension, preventing players from getting trapped between two jars
            DimMappingData dimMappingData = DimMappingData.from(serverLevel);
            JarDimData jarData = JarDimData.from(dimLevel);
            if (entity instanceof ServerPlayer && dimMappingData.getByKey(level.dimension().location()) == null) {
                jarData.setEnteredFrom(entity.getUUID(), GlobalPos.of(level.dimension(), entity.blockPosition()), entity.getRotationVector());
            }
            BlockPos spawnPos = jarData.getSpawnPos();
            entity.teleportTo(dimLevel, spawnPos.getX() + 0.5, spawnPos.getY() + 1, spawnPos.getZ() + 0.5, Set.of(), entity.getYRot(), entity.getXRot());
        }
    }

    // TODO breaking change: move to PlanariumRenderingTile
    public static class ClientDimEntry {
        private final StructureTemplate template;
        private final long lastUpdated;

        public ClientDimEntry(StructureTemplate template, long lastUpdated) {
            this.template = template;
            this.lastUpdated = lastUpdated;
        }

        // Record-like accessors for compatibility
        public StructureTemplate template() {
            return template;
        }

        public long lastUpdated() {
            return lastUpdated;
        }

    }

    // TODO breaking change: move to PlanariumRenderingTile
    public static class DimManager {
        public Map<ResourceKey<Level>, Entry> entries = new ConcurrentHashMap<>();

        public static void onBlockBroken(BlockEvent.BreakEvent event) {
            if (event.getLevel() instanceof ServerLevel level && WorldHelpers.isOfWorldType(level, ArsNouveau.DIMENSION_TYPE_KEY)) {
                boolean insideJar = PlanariumChunkGenerator.innerBox.contains(event.getPos().getBottomCenter());
                if (insideJar) {
                    PlanariumTile.dimManager.markDirty(level.dimension());
                }
                if (event.getState().getBlock() instanceof DimBoundary && !insideJar) {
                    if (event.getPlayer().canInteractWithBlock(event.getPos(), 4.0f)) {
                        DimBoundary.playerAttemptedBreak(level, event.getPlayer());
                    }
                    event.setCanceled(true);
                }
            }
        }

        public static void onBlockPlaced(BlockEvent.EntityPlaceEvent event) {
            if (event.getLevel() instanceof ServerLevel level && WorldHelpers.isOfWorldType(level, ArsNouveau.DIMENSION_TYPE_KEY)) {
                PlanariumTile.dimManager.markDirty(level.dimension());
            }
        }

        public @Nullable Tuple<ServerLevel, Entry> getExistingPlanarium(String dimName, ServerLevel serverLevel, BlockPos ownerPos) {
            DimMappingData.Entry dimEntry = DimMappingData.getExistingByName(serverLevel, dimName);
            if (dimEntry == null) {
                return null;
            }
            var dimKey = ResourceKey.create(Registries.DIMENSION, dimEntry.key());
            ServerLevel dimLevel = serverLevel.getServer().getLevel(dimKey);
            if (dimLevel == null) {
                return null;
            }
            return new Tuple<>(dimLevel, getOrCreateTemplate(serverLevel, ownerPos, dimKey));
        }

        public Tuple<ServerLevel, Entry> getOrCreateLevel(String dimName, ServerLevel serverLevel, BlockPos ownerPos) {
            DimMappingData.Entry dimEntry = DimMappingData.from(serverLevel.getServer().overworld()).getOrCreateByName(dimName);
            var dimKey = ResourceKey.create(Registries.DIMENSION, dimEntry.key());


            var newLevel = DimensionManager.INSTANCE.getOrCreateLevel(serverLevel.getServer(), dimKey, () -> createDimension(serverLevel.getServer()));
            var entry = getOrCreateTemplate(serverLevel, ownerPos, dimKey);

            return new Tuple<>(newLevel, entry);
        }
        public static LevelStem createDimension(MinecraftServer server) {
            return new LevelStem(getDimensionTypeHolder(server), new PlanariumChunkGenerator(server));
        }

        public static Holder<DimensionType> getDimensionTypeHolder(MinecraftServer server) {
            return server.registryAccess() // get dynamic registries
                    .registryOrThrow(Registries.DIMENSION_TYPE)
                    .getHolderOrThrow(ArsNouveau.DIMENSION_TYPE_KEY);
        }

        public StructureTemplate getTemplate(ResourceKey<Level> key) {
            var entry = entries.get(key);
            if (entry == null) {
                return null;
            }
            return entry.template;
        }

        public Entry getOrCreateTemplate(ServerLevel serverLevel, BlockPos worldPosition, ResourceKey<Level> key) {
            Entry entry = entries.get(key);
            if (entry == null) {
                StructureTemplate template = loadTemplate(serverLevel, worldPosition, key);
                if (template == null) {
                    return null;
                }
                entries.put(key, new Entry(template, false, serverLevel.getGameTime()));
                return entries.get(key);
            }

            if (!entry.dirty && entry.template != null) {
                return entry;
            }
            StructureTemplate template = loadTemplate(serverLevel, worldPosition, key);
            if (template == null) {
                return null;
            }
            entries.put(key, new Entry(template, false, serverLevel.getGameTime()));
            return entries.get(key);
        }

        private StructureTemplate loadTemplate(ServerLevel serverLevel, BlockPos worldPosition, ResourceKey<Level> key) {
            ServerLevel dimLevel = serverLevel.getServer().getLevel(key);
            if (dimLevel == null) {
                return null;
            }
            SectionPos chunkPos = SectionPos.of(BlockPos.ZERO);
            int chunkLoadingDistance = 3;
            forceLoad(chunkPos, chunkLoadingDistance, dimLevel, worldPosition, true);

            BlockPos pos = new BlockPos(0, 1, 0);
            Vec3i size = new Vec3i(32, 31, 32);
            StructureTemplate template = new StructureTemplate();
            template.fillFromWorld(dimLevel, pos, size, true, Blocks.AIR);
            forceLoad(chunkPos, chunkLoadingDistance, dimLevel, worldPosition, false);
            return template;
        }

        private void forceLoad(SectionPos chunkPos, int chunkLoadingDistance, ServerLevel dimLevel, BlockPos worldPosition, boolean load) {
            for (int x = chunkPos.getX() - chunkLoadingDistance; x <= chunkPos.getX() + chunkLoadingDistance; x++) {
                for (int z = chunkPos.getZ() - chunkLoadingDistance; z <= chunkPos.getZ() + chunkLoadingDistance; z++) {
                    ArsNouveau.ticketController.forceChunk(dimLevel, worldPosition, x, z, load, false);
                }
            }
        }

        public void markDirty(ResourceKey<Level> key) {
            if (entries.containsKey(key)) {
                entries.put(key, new Entry(entries.get(key).template, true));
            }
        }

        public record Entry(StructureTemplate template, boolean dirty, long lastUpdated) {
            public Entry(StructureTemplate template, boolean dirty) {
                this(template, dirty, 0L);
            }
        }
    }
}
