package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.common.world.dimension.PlanariumChunkGenerator;
import com.hollingsworth.arsnouveau.common.world.saved_data.DimMappingData;
import com.hollingsworth.arsnouveau.common.world.saved_data.JarDimData;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.commoble.infiniverse.internal.DimensionManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ArcanoJarTile extends ModdedTile implements ITickable {
    public static DimManager dimManager = new DimManager();
    public ResourceKey<Level> key;

    public ArcanoJarTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.ARCANO_JAR_TILE.get(), pos, state);
    }

    public void sendPlayer(Player entity) {
        if (key == null) {
            key = ResourceKey.create(Registries.DIMENSION, ArsNouveau.prefix("test_boss"));
        }
        if (key != null && level instanceof ServerLevel serverLevel) {
            ServerLevel dimLevel = dimManager.getOrCreateLevel(serverLevel, key, worldPosition);
            if (dimLevel == null) {
                return;
            }
            JarDimData jarData = JarDimData.from(dimLevel);
            if (entity instanceof ServerPlayer) {
                jarData.setEnteredFrom(entity.getUUID(), GlobalPos.of(level.dimension(), entity.blockPosition()), entity.getRotationVector());
            }
            BlockPos spawnPos = jarData.getSpawnPos();
            entity.teleportTo(dimLevel, spawnPos.getX() + 0.5, spawnPos.getY() + 1, spawnPos.getZ() + 0.5, Set.of(), entity.getYRot(), entity.getXRot());
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (key != null) {
            tag.putString("key", key.location().toString());
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("key"))
            key = ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(tag.getString("key")));
    }

    public static class DimManager {
        public Map<ResourceKey<Level>, PlanariumTile.DimManager.Entry> entries = new ConcurrentHashMap<>();


        public ServerLevel getOrCreateLevel(String dimName, ServerLevel serverLevel, BlockPos ownerPos) {
            DimMappingData.Entry dimEntry = DimMappingData.from(serverLevel.getServer().overworld()).getOrCreateByName(dimName);
            var dimKey = ResourceKey.create(Registries.DIMENSION, dimEntry.key());
            return getOrCreateLevel(serverLevel, dimKey, ownerPos);
        }

        public ServerLevel getOrCreateLevel(ServerLevel serverLevel, ResourceKey<Level> key, BlockPos ownerPos) {
            return DimensionManager.INSTANCE.getOrCreateLevel(serverLevel.getServer(), key, () -> {
                return createDimension(serverLevel.getServer());
            });
        }

        public static LevelStem createDimension(MinecraftServer server) {
            return new LevelStem(getDimensionTypeHolder(server), new PlanariumChunkGenerator(server));
        }

        public static Holder<DimensionType> getDimensionTypeHolder(MinecraftServer server) {
            return server.registryAccess() // get dynamic registries
                    .registryOrThrow(Registries.DIMENSION_TYPE)
                    .getHolderOrThrow(ArsNouveau.ARCANO_DIMENSION_TYPE_KEY);
        }
    }
}
