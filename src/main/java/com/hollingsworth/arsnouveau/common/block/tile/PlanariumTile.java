package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketUpdateDimTile;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectName;
import com.hollingsworth.arsnouveau.common.world.dimension.VoidChunkGenerator;
import com.hollingsworth.arsnouveau.common.world.saved_data.DimMappingData;
import com.hollingsworth.arsnouveau.common.world.saved_data.JarDimData;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.hollingsworth.nuggets.common.util.BlockPosHelpers;
import com.hollingsworth.nuggets.common.util.WorldHelpers;
import net.commoble.infiniverse.internal.DimensionManager;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
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

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class PlanariumTile extends ModdedTile implements ITickable, GeoBlockEntity {

    public static DimManager dimManager = new DimManager();
    public ResourceKey<Level> key;
    private StructureTemplate template;
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
        if (template != null && level.getGameTime() % 40 == 0) {
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
        if (name != null) {
            tag.putString("name", Component.Serializer.toJson(this.name, registries));
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("key"))
            key = ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(tag.getString("key")));
        if (tag.contains("name")) {
            this.name = parseCustomNameSafe(tag.getString("name"), registries);
        }
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

    @Override
    protected void applyImplicitComponents(BlockEntity.DataComponentInput componentInput) {
        super.applyImplicitComponents(componentInput);
        this.name = componentInput.get(DataComponents.CUSTOM_NAME);
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        components.set(DataComponents.CUSTOM_NAME, this.name);
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
            this.template = managerEntry.template;
            lastUpdated = managerEntry.lastUpdated;
            Networking.sendToNearbyClient(level, worldPosition, new PacketUpdateDimTile(worldPosition, template));
            updateBlock();
        }
        return dimension.getA();
    }

    public void sendEntityTo(Entity entity) {
        if (key != null && level instanceof ServerLevel serverLevel) {
            ServerLevel dimLevel = level.getServer().getLevel(key);
            DimMappingData dimMappingData = DimMappingData.from(serverLevel);
            if (entity instanceof ServerPlayer && dimMappingData.getByKey(level.dimension().location()) == null) {
                JarDimData jarData = JarDimData.from(dimLevel);
                jarData.setEnteredFrom(entity.getUUID(), GlobalPos.of(level.dimension(), entity.blockPosition()), entity.getRotationVector());
            }
            entity.teleportTo(dimLevel, 7, 2, 7, Set.of(), entity.getYRot(), entity.getXRot());
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

    public static class DimManager {
        public Map<ResourceKey<Level>, Entry> entries = new ConcurrentHashMap<>();

        public static void onBlockBroken(BlockEvent.BreakEvent event) {
            if (event.getLevel() instanceof Level level && WorldHelpers.isOfWorldType(level, ArsNouveau.DIMENSION_TYPE_KEY)) {
                System.out.println("broke in dim!");
            }
        }

        public static void onBlockPlaced(BlockEvent.EntityPlaceEvent event) {
            if (event.getLevel() instanceof Level level && WorldHelpers.isOfWorldType(level, ArsNouveau.DIMENSION_TYPE_KEY)) {
                System.out.println("placed in dim!");
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
            int chunkLoadingDistance = 3;
            forceLoad(chunkPos, chunkLoadingDistance, dimLevel, worldPosition, true);

            BlockPos pos = new BlockPos(0, 1, 0);
            Vec3i size = new Vec3i(32, 31, 32);
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
