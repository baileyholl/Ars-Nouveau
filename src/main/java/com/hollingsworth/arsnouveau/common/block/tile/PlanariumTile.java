package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.client.renderer.ExtendedRenderingWorld;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.common.mixin.structure.StructureTemplateAccessor;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketClientRequestDim;
import com.hollingsworth.arsnouveau.common.network.PacketUpdateDimTile;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectName;
import com.hollingsworth.arsnouveau.common.world.dimension.VoidChunkGenerator;
import com.hollingsworth.arsnouveau.common.world.saved_data.DimMappingData;
import com.hollingsworth.arsnouveau.common.world.saved_data.JarDimData;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.hollingsworth.nuggets.client.rendering.FakeRenderingWorld;
import com.hollingsworth.nuggets.client.rendering.StatePos;
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
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class PlanariumTile extends ModdedTile implements ITickable, Nameable, GeoBlockEntity {

    public static DimManager dimManager = new DimManager();
    public static Map<ResourceKey<Level>, ClientDimEntry> clientTemplates = new ConcurrentHashMap<>();

    public ResourceKey<Level> key;
    private long lastUpdated = 0;
    boolean playersNearby = true;
    public Component name;
    public boolean isDimModel = false;


    public PlanariumTile(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
    }

    public PlanariumTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.PLANARIUM_TILE.get(), pos, state);
    }

    @Override
    public void tick() {
        if (key == null)
            return;

        if (level instanceof ServerLevel serverLevel) {
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
                // Eagerly sends a packet any time this dimension gets marked dirty.
                // Consider adding a backoff period for batching template changes
                if (entry1.lastUpdated > lastUpdated) {
                    lastUpdated = entry1.lastUpdated;
                    StructureTemplate template = dimManager.getTemplate(key);
                    if (template != null) {
                        Networking.sendToNearbyClient(level, worldPosition, new PacketUpdateDimTile(worldPosition, template));
                    }
                }
            }
        }
    }

    public void setTemplateClientSide(StructureTemplate template) {
        if (level.isClientSide && key != null) {
            ArrayList<StatePos> statePos = new ArrayList<>();
            var palette = ((StructureTemplateAccessor) template).getPalettes().get(0);
            for (StructureTemplate.StructureBlockInfo blockInfo : palette.blocks()) {
                statePos.add(new StatePos(blockInfo.state(), blockInfo.pos()));
            }
            PlanariumTile.clientTemplates.put(key, new ClientDimEntry(template, statePos, level.getGameTime(), new ExtendedRenderingWorld(this.level, statePos, BlockPos.ZERO)));
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        updateBlock();
    }


    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        super.handleUpdateTag(tag, lookupProvider);
        // Checks if the client is up to date with this template compared to
        if (level.isClientSide) {
            if (key != null && (!clientTemplates.containsKey(key) || clientTemplates.get(key).lastUpdated < lastUpdated)) {
                Networking.sendToServer(new PacketClientRequestDim(worldPosition));
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (key != null) {
            tag.putString("key", key.location().toString());
        }
        if (name != null) {
            tag.putString("name", name.getString());
        }

        tag.putLong("lastUpdated", lastUpdated);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("key"))
            key = ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(tag.getString("key")));
        if (tag.contains("name")) {
            this.name = Component.literal(tag.getString("name"));
        }
        lastUpdated = tag.getLong("lastUpdated");
    }

    public @Nullable StructureTemplate getTemplate() {
        if (key == null) {
            return null;
        }
        if (level.isClientSide) {
            var entry = PlanariumTile.clientTemplates.get(key);
            return entry == null ? null : entry.template();
        } else {
            return PlanariumTile.dimManager.getTemplate(key);
        }
    }

    @Override
    protected void applyImplicitComponents(BlockEntity.DataComponentInput componentInput) {
        super.applyImplicitComponents(componentInput);
        this.name = componentInput.get(DataComponents.CUSTOM_NAME);
        if (name != null && level instanceof ServerLevel serverLevel) {
            this.setDimension(name.getString(), serverLevel);
        }
    }

    public void setName(Component name) {
        this.name = name;
        if (level instanceof ServerLevel serverLevel) {
            if (name == null) {
                this.key = null;
            } else {
                this.setDimension(name.getString(), serverLevel);
            }
        }
    }

    @Override
    public @Nullable Component getCustomName() {
        return name;
    }


    public IResolveListener onResolve() {
        return new IResolveListener() {
            @Override
            public void onPostResolve(Level world, @NotNull LivingEntity shooter, HitResult result, Spell spell, SpellContext spellContext, AbstractEffect resolveEffect, SpellStats spellStats, SpellResolver spellResolver) {
                if (world instanceof ServerLevel serverLevel && resolveEffect instanceof EffectName && spell.name() != null) {
                    name = Component.literal(spell.name());
                    setDimension(spell.name(), serverLevel);
                }
            }
        };
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

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }

    AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public Component getName() {
        return name;
    }


    public record ClientDimEntry(StructureTemplate template, List<StatePos> statePosList, long lastUpdated,
                                 FakeRenderingWorld fakeRenderingWorld) {

    }

    public static class DimManager {
        public Map<ResourceKey<Level>, Entry> entries = new ConcurrentHashMap<>();

        public static void onBlockBroken(BlockEvent.BreakEvent event) {
            if (event.getLevel() instanceof ServerLevel level && WorldHelpers.isOfWorldType(level, ArsNouveau.DIMENSION_TYPE_KEY)) {
                PlanariumTile.dimManager.markDirty(level.dimension());
            }
        }

        public static void onBlockPlaced(BlockEvent.EntityPlaceEvent event) {
            if (event.getLevel() instanceof ServerLevel level && WorldHelpers.isOfWorldType(level, ArsNouveau.DIMENSION_TYPE_KEY)) {
                PlanariumTile.dimManager.markDirty(level.dimension());
            }
        }

        public Tuple<ServerLevel, Entry> getOrCreateLevel(String dimName, ServerLevel serverLevel, BlockPos ownerPos) {
            DimMappingData.Entry dimEntry = DimMappingData.from(serverLevel.getServer().overworld()).getOrCreateByName(dimName);
            var dimKey = ResourceKey.create(Registries.DIMENSION, dimEntry.key());
            return getOrCreateLevel(serverLevel, dimKey, ownerPos);
        }

        public Tuple<ServerLevel, Entry> getOrCreateLevel(ServerLevel serverLevel, ResourceKey<Level> key, BlockPos ownerPos) {
            var newLevel = DimensionManager.INSTANCE.getOrCreateLevel(serverLevel.getServer(), key, () -> {
                return createDimension(serverLevel.getServer());
            });
            var entry = getOrCreateTemplate(serverLevel, ownerPos, key);

            return new Tuple<>(newLevel, entry);
        }

        public static LevelStem createDimension(MinecraftServer server) {
            return new LevelStem(getDimensionTypeHolder(server), new VoidChunkGenerator(server));
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
