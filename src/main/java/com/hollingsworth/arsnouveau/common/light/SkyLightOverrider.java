package com.hollingsworth.arsnouveau.common.light;

import com.hollingsworth.arsnouveau.common.mixin.light.LayerLightSectionStorageAccessor;
import com.hollingsworth.arsnouveau.common.mixin.light.LevelLightEngineAccessor;
import com.hollingsworth.arsnouveau.common.mixin.light.SkyLightEngineAccessor;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;
import java.util.WeakHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.lighting.LightEngine;

import static com.hollingsworth.arsnouveau.ArsNouveau.MODID;

public class SkyLightOverrider {
    public class SourceEntry {
        public final ISkyLightSource source;
        public final BlockPos position;
        public final boolean isFloorKey;
        public final boolean isCeilingKey;

        private SourceEntry(ISkyLightSource source, BlockPos position, boolean isFloorKey, boolean isCeilingKey) {
            this.source = source;
            this.position = position.immutable();
            this.isFloorKey = isFloorKey;
            this.isCeilingKey = isCeilingKey;
        }

        public static SourceEntry of(SkyLightOverrider owner, Block block, BlockPos position) {
            if ((block instanceof ISkyLightSource source) && position != null) {
                return owner.new SourceEntry(source, position, false, false);
            }
            return null;
        }

        public static SourceEntry floorKey(SkyLightOverrider owner, int y) {
            return owner.new SourceEntry(null, new BlockPos(0, y, 0), true, false);
        }

        public static SourceEntry ceilingKey(SkyLightOverrider owner, int y) {
            return owner.new SourceEntry(null, new BlockPos(0, y, 0), false, true);
        }

        public static int compareByY(SourceEntry lhs, SourceEntry rhs) {
            if (!lhs.getLevel().equals(rhs.getLevel())) {
                return System.identityHashCode(lhs) - System.identityHashCode(rhs);
            }
            if (lhs == rhs) {
                return 0;
            }
            int diff = lhs.position.getY() - rhs.position.getY();
            if (diff < 0) {
                return -1;
            } else if (diff > 0) {
                return 1;
            }
            if (lhs.isFloorKey || rhs.isCeilingKey) {
                if (lhs.isCeilingKey || rhs.isFloorKey) {
                    return 0;
                }
                // NavigableSet.floor should return the greatest element <= argument
                return 1;
            }
            if (lhs.isCeilingKey || rhs.isFloorKey) {
                // NavigableSet.ceiling should return the least element >= argument
                return -1;
            }
            return System.identityHashCode(lhs) - System.identityHashCode(rhs);
        }

        public Level getLevel() {
            return level;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof SourceEntry other) {
                if (!getLevel().equals(other.getLevel())) {
                    return false;
                }
                if (isFloorKey && other.isFloorKey || isCeilingKey && other.isCeilingKey) {
                    return position.getY() == other.position.getY();
                }
            }
            return super.equals(obj);
        }

        @Override
        public int hashCode() {
            if (isFloorKey) {
                return (position.getY() << 1) ^ -1 ^ getLevel().hashCode();
            }
            if (isCeilingKey) {
                return (position.getY() << 1) ^ -2 ^ getLevel().hashCode();
            }
            return super.hashCode();
        }

        public boolean stillExists() {
            BlockState blockState = level.getBlockState(position);
            return blockState.getBlock().equals(source);
        }

        public boolean isActive() {
            BlockState blockState = level.getBlockState(position);
            return source.emitsDirectSkyLight(blockState, getLevel(), position);
        }
    }

    public record ChunkLocalColumnPos(short localX, short localZ) {
        public ChunkLocalColumnPos(BlockPos blockPos) {
            this((short) (blockPos.getX() & 0xF), (short) (blockPos.getZ() & 0xF));
        }

        public BlockPos getBlockPos(ChunkPos chunk, int y) {
            return chunk.getBlockAt(localX(), y, localZ());
        }
    }

    static Map<Level, SkyLightOverrider> perLevelInstances = new WeakHashMap();

    // Private constants of SkyLightEngine
    public static final long REMOVE_SKY_SOURCE_ENTRY = LightEngine.QueueEntry.decreaseSkipOneDirection(15, Direction.UP);
    public static final long ADD_SKY_SOURCE_ENTRY = LightEngine.QueueEntry.increaseSkipOneDirection(15, false, Direction.UP);
    // Custom constants
    public static final long REMOVE_TOP_ARTIFICIAL_SKY_SOURCE_ENTRY = LightEngine.QueueEntry.decreaseAllDirections(15);
    public static final long ADD_TOP_ARTIFICIAL_SKY_SOURCE_ENTRY = LightEngine.QueueEntry.increaseSkipOneDirection(15, false, Direction.DOWN);

    public final Level level;
    protected final int MAX_LIGHT_LEVEL;
    protected Map<LevelChunk, Map<ChunkLocalColumnPos, NavigableSet<SourceEntry>>> sourceStorage = new WeakHashMap();

    private SkyLightOverrider(Level level) {
        this.level = level;
        this.MAX_LIGHT_LEVEL = level.getMaxLightLevel();
    }

    public static SkyLightOverrider forLevel(Level level) {
        synchronized (perLevelInstances) {
            return perLevelInstances.computeIfAbsent(level, SkyLightOverrider::new);
        }
    }

    protected NavigableSet<SourceEntry> getSourceColumn(BlockPos pos) {
        LevelChunk chunk = level.getChunkAt(pos);
        Map<ChunkLocalColumnPos, NavigableSet<SourceEntry>> chunkLocalStorage = sourceStorage.computeIfAbsent(chunk, c -> new HashMap());
        ChunkLocalColumnPos columnOffset = new ChunkLocalColumnPos(pos);
        Comparator<SourceEntry> cmp = SourceEntry::compareByY;
        return chunkLocalStorage.computeIfAbsent(columnOffset, o -> new TreeSet<SourceEntry>(cmp));
    }

    protected NavigableSet<SourceEntry> getSourceColumnIfExists(BlockPos pos) {
        LevelChunk chunk = level.getChunkAt(pos);
        Map<ChunkLocalColumnPos, NavigableSet<SourceEntry>> chunkLocalStorage = sourceStorage.get(chunk);
        if (chunkLocalStorage == null) {
            return null;
        }
        ChunkLocalColumnPos columnOffset = new ChunkLocalColumnPos(pos);
        return chunkLocalStorage.get(columnOffset);
    }

    protected boolean checkSourceExists(SourceEntry entry, Iterator<SourceEntry> it, SkyLightEngineAccessor engine) {
        if (entry.stillExists()) {
            return true;
        }
        it.remove();
        ((LightEngine) engine).checkBlock(entry.position);
        return false;
    }

    /**
     * Called by SkyLightEngine before it actually handles positions scheduled by {@link SkyLightEngine#checkBlock}
     * @param skyEngine mush be equal to this.level.getLightEngine().getSkyEngine()
     * @param pos the position that was scheduled by {@link SkyLightEngine#checkBlock}
     * @return true if the normal checking code should be skipped
     */
    public boolean beforeCheckNode(SkyLightEngineAccessor skyEngine, BlockPos pos) {
        BlockState blockState = skyEngine.callGetState(pos);
        Block block = blockState.getBlock();
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        LayerLightSectionStorageAccessor storage = (LayerLightSectionStorageAccessor) skyEngine.getStorage();

        if (block instanceof ISkyLightSource source) {
            NavigableSet<SourceEntry> column = getSourceColumn(pos);
            Set<SourceEntry> candidates = column.subSet(SourceEntry.ceilingKey(this, y), SourceEntry.floorKey(this, y));
            for (Iterator<SourceEntry> it = candidates.iterator(); it.hasNext();) {
                checkSourceExists(it.next(), it, skyEngine);
            }
            if (candidates.isEmpty()) {
                column.add(SourceEntry.of(this, block, pos));
            }
        }

        NavigableSet<SourceEntry> column = getSourceColumnIfExists(pos);
        if (column == null) {
            return false;
        }

        boolean underSource = false;
        for (Iterator<SourceEntry> it = column.iterator(); it.hasNext();) {
            SourceEntry source = it.next();
            int surfaceY = findSurfaceBelow(skyEngine, source.position);
            if (!checkSourceExists(source, it, skyEngine) || !source.isActive()) {
                skyEngine.callRemoveSourcesBelow(x, z, source.position.getY(), surfaceY);
                skyEngine.callEnqueueDecrease(source.position.asLong(), REMOVE_TOP_ARTIFICIAL_SKY_SOURCE_ENTRY);
                continue;
            }
            long packedSourcePos = source.position.asLong();
            long packedGuardPos = source.position.offset(0, 1, 0).asLong();
            if (!storage.callLightOnInSection(SectionPos.blockToSection(packedSourcePos))) {
                continue;
            }
            if (storage.get(packedSourcePos) < MAX_LIGHT_LEVEL) {
                storage.set(packedSourcePos, MAX_LIGHT_LEVEL);
                skyEngine.callEnqueueIncrease(packedSourcePos, ADD_TOP_ARTIFICIAL_SKY_SOURCE_ENTRY);  // FIXME it seems to only propagate side/upward when something else caused the column update later
            }
            if (storage.callLightOnInSection(SectionPos.blockToSection(packedSourcePos)) && storage.get(packedGuardPos) >= MAX_LIGHT_LEVEL) {
                // The block just above was under real sky or another skylight source
                // We need to make sure that if it's no longer the case
                // Then the caused update will stop before it reaches the current source
                // Else it will be restored to the max level
                storage.set(packedSourcePos, MAX_LIGHT_LEVEL - 1);
            }
            skyEngine.callUpdateSourcesInColumn(x, z, surfaceY);
            if (surfaceY - 1 <= y && y < source.position.getY())  {
                underSource = true;
            }
        }

        if (underSource) {
            int realSurfaceY = skyEngine.callGetLowestSourceY(x, z, Integer.MAX_VALUE);
            skyEngine.callUpdateSourcesInColumn(x, z, realSurfaceY);  // Because we are cancelling the vanilla handler
            long packedPos = pos.asLong();
            skyEngine.callEnqueueDecrease(packedPos, REMOVE_SKY_SOURCE_ENTRY);
            skyEngine.callEnqueueIncrease(packedPos, ADD_SKY_SOURCE_ENTRY);
            return true;
        }

        return false;
    }

    protected int findSurfaceBelow(SkyLightEngineAccessor skyEngine, BlockPos topPos) {
        final int minY = level.dimensionType().minY();
        BlockPos prevPos = topPos;
        BlockState prevBlockState = skyEngine.callGetState(prevPos);
        int y;
        for (y = topPos.getY() - 1; y >= minY; --y) {
            BlockPos pos = topPos.atY(y);
            BlockState blockState = skyEngine.callGetState(pos);
            if (blockState.getLightBlock(level, pos) > 0) {
                break;
            }
            if (skyEngine.callShapeOccludes(prevPos.asLong(), prevBlockState, pos.asLong(), blockState, Direction.DOWN)) {
                break;
            }
            prevPos = pos;
            prevBlockState = blockState;
        }
        return y + 1;
    }
}
