package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketUpdateDimTile;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.hollingsworth.nuggets.common.util.BlockPosHelpers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.SectionPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DimTile extends ModdedTile implements ITickable {

    public static DimManager dimManager = new DimManager();
    public ResourceKey<Level> key;
    private StructureTemplate template;
    private long lastUpdated = 0;
    boolean playersNearby = true;

    public DimTile(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
    }

    public DimTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.DIM_TILE.get(), pos, state);
    }

    @Override
    public void tick() {
        if (key == null || !(level instanceof ServerLevel serverLevel))
            return;
        if (level.getGameTime() % 200 == 0) {
            playersNearby = false;
            for (ServerPlayer serverPlayer : serverLevel.players()) {
                if (BlockPosHelpers.distanceBetween(worldPosition, serverPlayer.blockPosition()) < 64) {
                    playersNearby = true;
                    break;
                }
            }
        }
        if (!playersNearby) {
            return;
        }
        DimManager.Entry entry1 = dimManager.getOrCreateTemplate(serverLevel, worldPosition, key);
        if (entry1 != null) {
            this.template = entry1.template;
            if (entry1.lastUpdated > lastUpdated) {
                lastUpdated = entry1.lastUpdated;
                updateBlock();
            }
        }
        if (template != null && level.getGameTime() % 200 == 0) {
            Networking.sendToNearbyClient(level, worldPosition, new PacketUpdateDimTile(worldPosition, template));
        }
    }

    public void setTemplateClientSide(StructureTemplate template) {
        if (level.isClientSide) {
            this.template = template;
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        updateBlock();
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

    @Override
    public @Nullable ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this, (tile, registryAccess) -> {
            CompoundTag tag = new CompoundTag();
            if (template != null) {
                CompoundTag templateTag = new CompoundTag();
                template.save(templateTag);
                tag.put("template", templateTag);
            }
            return tag;
        });
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        super.handleUpdateTag(tag, lookupProvider);
        if (tag.contains("template")) {
            template = new StructureTemplate();
            template.load(BuiltInRegistries.BLOCK.asLookup(), tag.getCompound("template"));
        } else {
            template = null;
        }
    }

    public StructureTemplate getTemplate() {
        return template;
    }

    public static class DimManager {
        public Map<ResourceKey<Level>, Entry> entries = new ConcurrentHashMap<>();

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

            long timeSinceLastUpdate = serverLevel.getGameTime() - entry.lastUpdated;

            if (!entry.dirty && entry.template != null && timeSinceLastUpdate < 200) {
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
            int chunkLoadingDistance = 5;
            forceLoad(chunkPos, chunkLoadingDistance, dimLevel, worldPosition, true);

            BlockPos pos = BlockPos.ZERO;
            Vec3i size = new Vec3i(16, 16, 16);
            StructureTemplate template = new StructureTemplate();
            template.fillFromWorld(dimLevel, pos, size, true, null);
            forceLoad(chunkPos, chunkLoadingDistance, dimLevel, worldPosition, false);
            return template;
        }

        private void forceLoad(SectionPos chunkPos, int chunkLoadingDistance, ServerLevel dimLevel, BlockPos worldPosition, boolean load) {
            for (int x = chunkPos.getX() - chunkLoadingDistance; x <= chunkPos.getX() + chunkLoadingDistance; x++) {
                for (int z = chunkPos.getZ() - chunkLoadingDistance; z <= chunkPos.getZ() + chunkLoadingDistance; z++) {
                    ArsNouveau.ticketController.forceChunk(dimLevel, worldPosition, x, z, load, load);
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
