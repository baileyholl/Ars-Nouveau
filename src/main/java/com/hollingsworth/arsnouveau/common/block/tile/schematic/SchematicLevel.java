package com.hollingsworth.arsnouveau.common.block.tile.schematic;

import com.hollingsworth.arsnouveau.common.util.Log;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.AbortableIterationConsumer;
import net.minecraft.world.TickRateManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.entity.LevelEntityGetter;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.storage.WritableLevelData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.ticks.BlackholeTickAccess;
import net.minecraft.world.ticks.LevelTickAccess;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public class SchematicLevel extends Level implements ServerLevelAccessor {
    private static final Set<DataComponentType<?>> SAFE_ITEM_COMPONENTS = Set.of(DataComponents.ENCHANTMENTS, DataComponents.POTION_CONTENTS, DataComponents.DAMAGE, DataComponents.CUSTOM_NAME, DataComponents.MAP_ID);

    private static final LevelEntityGetter<Entity> NO_ENTITIES = new LevelEntityGetter<>() {
        @Nullable
        @Override
        public Entity get(int id) {
            return null;
        }

        @Nullable
        @Override
        public Entity get(UUID uuid) {
            return null;
        }

        @Override
        public Iterable<Entity> getAll() {
            return List.of();
        }

        @Override
        public <U extends Entity> void get(EntityTypeTest<Entity, U> test, AbortableIterationConsumer<U> consumer) {
        }

        @Override
        public void get(AABB bounds, Consumer<Entity> consumer) {
        }

        @Override
        public <U extends Entity> void get(EntityTypeTest<Entity, U> test, AABB bounds, AbortableIterationConsumer<U> consumer) {
        }
    };

    protected final Level level;
    protected final SchematicChunkSource chunkSource;
    protected final Map<BlockPos, BlockState> blocks = new HashMap<>();
    protected final Map<BlockPos, BlockEntity> blockEntities = new HashMap<>();
    protected final List<Entity> entities = new ArrayList<>();
    protected final BoundingBox bounds = new BoundingBox(BlockPos.ZERO);
    public final BlockPos anchor;

    public SchematicLevel(BlockPos anchor, Level original) {
        super((WritableLevelData) original.getLevelData(), original.dimension(), original.registryAccess(), original.dimensionTypeRegistration(),
                original::getProfiler, original.isClientSide, original.isDebug(), 0, 0);
        this.level = original;
        this.anchor = anchor;
        this.chunkSource = new SchematicChunkSource(this);
    }

    public static ItemStack withUnsafeComponentsDiscarded(ItemStack stack) {
        DataComponentPatch patch = stack.getComponentsPatch();
        if (patch.isEmpty())
            return stack;
        ItemStack copy = new ItemStack(stack.getItemHolder(), stack.getCount());
        copy.applyComponents(patch.forget(type -> !SAFE_ITEM_COMPONENTS.contains(type)));
        return copy;
    }

    public Set<BlockPos> getAllPositions() {
        return blocks.keySet();
    }

    public Map<BlockPos, BlockState> getBlockMap() {
        return blocks;
    }

    public List<Entity> getEntityList() {
        return entities;
    }

    public BoundingBox getBounds() {
        return bounds;
    }

    @Override
    public boolean addFreshEntity(Entity entity) {
        if (entity instanceof ItemFrame itemFrame) {
            ItemStack safe = withUnsafeComponentsDiscarded(itemFrame.getItem());
            if (safe != itemFrame.getItem())
                itemFrame.setItem(safe, false);
        }
        if (entity instanceof ArmorStand armorStand) {
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                ItemStack safe = withUnsafeComponentsDiscarded(armorStand.getItemBySlot(slot));
                if (safe != armorStand.getItemBySlot(slot))
                    armorStand.setItemSlot(slot, safe);
            }
        }
        return entities.add(entity);
    }

    @Override
    public BlockState getBlockState(BlockPos globalPos) {
        BlockPos pos = globalPos.subtract(anchor);
        if (pos.getY() - bounds.minY() == -1)
            return Blocks.DIRT.defaultBlockState();
        BlockState state = blocks.get(pos);
        if (state == null)
            return Blocks.AIR.defaultBlockState();
        if (state.getBlock() instanceof AbstractFurnaceBlock && state.hasProperty(BlockStateProperties.LIT))
            return state.setValue(BlockStateProperties.LIT, false);
        return state;
    }

    @Override
    public FluidState getFluidState(BlockPos pos) {
        return getBlockState(pos).getFluidState();
    }

    @Nullable
    @Override
    public BlockEntity getBlockEntity(BlockPos globalPos) {
        BlockPos pos = globalPos.subtract(anchor);
        BlockEntity existing = blockEntities.get(pos);
        if (existing != null)
            return existing;
        BlockState state = blocks.get(pos);
        if (state == null || !state.hasBlockEntity())
            return null;
        try {
            BlockEntity blockEntity = ((EntityBlock) state.getBlock()).newBlockEntity(globalPos, state);
            if (blockEntity != null) {
                blockEntity.setLevel(this);
                blockEntities.put(pos, blockEntity);
            }
            return blockEntity;
        } catch (Exception e) {
            Log.getLogger().debug("Could not create block entity for schematic block {}", state, e);
            return null;
        }
    }

    @Override
    public boolean setBlock(BlockPos globalPos, BlockState state, int flags, int recursionLeft) {
        BlockPos pos = globalPos.subtract(anchor);
        bounds.encapsulate(pos);
        blocks.put(pos, state);
        BlockEntity existing = blockEntities.get(pos);
        if (existing != null && !existing.getType().isValid(state))
            blockEntities.remove(pos);
        return true;
    }

    @Override
    public void sendBlockUpdated(BlockPos pos, BlockState oldState, BlockState newState, int flags) {
    }

    @Override
    public void updateNeighborsAt(BlockPos pos, Block block) {
    }

    @Override
    public void updateNeighborsAtExceptFromFacing(BlockPos pos, Block blockType, Direction skipSide) {
    }

    @Override
    public void neighborChanged(BlockPos pos, Block block, BlockPos fromPos) {
    }

    @Override
    public void neighborChanged(BlockState state, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
    }

    @Override
    public void neighborShapeChanged(Direction direction, BlockState queried, BlockPos pos, BlockPos offsetPos, int flags, int recursionLevel) {
    }

    @Override
    public LevelLightEngine getLightEngine() {
        return level.getLightEngine();
    }

    @Override
    public int getBrightness(LightLayer lightLayer, BlockPos pos) {
        return 15;
    }

    @Override
    public int getMaxLocalRawBrightness(BlockPos pos) {
        return 15;
    }

    @Override
    public float getShade(Direction direction, boolean shade) {
        return level.getShade(direction, shade);
    }

    @Override
    public LevelTickAccess<Block> getBlockTicks() {
        return BlackholeTickAccess.emptyLevelList();
    }

    @Override
    public LevelTickAccess<Fluid> getFluidTicks() {
        return BlackholeTickAccess.emptyLevelList();
    }

    @Override
    public ChunkSource getChunkSource() {
        return chunkSource;
    }

    @Override
    public void levelEvent(@Nullable Player player, int type, BlockPos pos, int data) {
    }

    @Override
    public void gameEvent(Holder<GameEvent> gameEvent, Vec3 pos, GameEvent.Context context) {
    }

    @Override
    public List<? extends Player> players() {
        return List.of();
    }

    @Override
    public void playSeededSound(@Nullable Player player, double x, double y, double z, Holder<SoundEvent> sound, SoundSource source, float volume, float pitch, long seed) {
    }

    @Override
    public void playSeededSound(@Nullable Player player, Entity entity, Holder<SoundEvent> sound, SoundSource source, float volume, float pitch, long seed) {
    }

    @Nullable
    @Override
    public Entity getEntity(int id) {
        return null;
    }

    @Override
    public TickRateManager tickRateManager() {
        return level.tickRateManager();
    }

    @Nullable
    @Override
    public MapItemSavedData getMapData(MapId mapId) {
        return null;
    }

    @Override
    public void setMapData(MapId mapId, MapItemSavedData mapData) {
    }

    @Override
    public MapId getFreeMapId() {
        return level.getFreeMapId();
    }

    @Override
    public void destroyBlockProgress(int breakerId, BlockPos pos, int progress) {
    }

    @Override
    public Scoreboard getScoreboard() {
        return level.getScoreboard();
    }

    @Override
    public RecipeManager getRecipeManager() {
        return level.getRecipeManager();
    }

    @Override
    public Holder<Biome> getUncachedNoiseBiome(int x, int y, int z) {
        return level.getUncachedNoiseBiome(x, y, z);
    }

    @Override
    public PotionBrewing potionBrewing() {
        return level.potionBrewing();
    }

    @Override
    public FeatureFlagSet enabledFeatures() {
        return level.enabledFeatures();
    }

    @Override
    public String gatherChunkSourceStats() {
        return level.gatherChunkSourceStats();
    }

    @Override
    protected LevelEntityGetter<Entity> getEntities() {
        return NO_ENTITIES;
    }

    @Override
    public ServerLevel getLevel() {
        if (level instanceof ServerLevel serverLevel)
            return serverLevel;
        throw new IllegalStateException("SchematicLevel wraps a client level and has no ServerLevel");
    }

    // Copied from LevelHeightAccessor so mods that override the vanilla height methods (such as Lithium) cannot break this wrapper
    @Override
    public int getMaxBuildHeight() {
        return getMinBuildHeight() + getHeight();
    }

    @Override
    public int getSectionsCount() {
        return getMaxSection() - getMinSection();
    }

    @Override
    public int getMinSection() {
        return SectionPos.blockToSectionCoord(getMinBuildHeight());
    }

    @Override
    public int getMaxSection() {
        return SectionPos.blockToSectionCoord(getMaxBuildHeight() - 1) + 1;
    }

    @Override
    public boolean isOutsideBuildHeight(BlockPos pos) {
        return isOutsideBuildHeight(pos.getY());
    }

    @Override
    public boolean isOutsideBuildHeight(int y) {
        return y < getMinBuildHeight() || y >= getMaxBuildHeight();
    }

    @Override
    public int getSectionIndex(int y) {
        return getSectionIndexFromSectionY(SectionPos.blockToSectionCoord(y));
    }

    @Override
    public int getSectionIndexFromSectionY(int sectionY) {
        return sectionY - getMinSection();
    }

    @Override
    public int getSectionYFromSectionIndex(int sectionIndex) {
        return sectionIndex + getMinSection();
    }

    @Override
    public void setDayTimeFraction(float dayTimeFraction) {
    }

    @Override
    public float getDayTimeFraction() {
        return 0;
    }

    @Override
    public float getDayTimePerTick() {
        return 0;
    }

    @Override
    public void setDayTimePerTick(float dayTimePerTick) {
    }
}
