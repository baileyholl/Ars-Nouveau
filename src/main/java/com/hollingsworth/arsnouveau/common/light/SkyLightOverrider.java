package com.hollingsworth.arsnouveau.common.light;

import com.hollingsworth.arsnouveau.common.mixin.light.LayerLightSectionStorageAccessor;
import com.hollingsworth.arsnouveau.common.mixin.light.LevelLightEngineAccessor;
import com.hollingsworth.arsnouveau.common.mixin.light.SkyLightEngineAccessor;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
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
    public final int MAX_LIGHT_LEVEL;
    protected Map<LevelChunk, Map<ChunkLocalColumnPos, NavigableSet<SourceEntry>>> sourceStorage = new WeakHashMap();
    protected Map<LevelChunk, Map<ChunkLocalColumnPos, NavigableMap<Integer, Integer>>> transparentRunCache = new WeakHashMap();  // Maps the lowest transparent block in a run to some block in the same run or the non-transparent one immediately above

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

    protected NavigableMap<Integer, Integer> getTransparentRunCacheColumn(BlockPos pos) {
        LevelChunk chunk = level.getChunkAt(pos);
        Map<ChunkLocalColumnPos, NavigableMap<Integer, Integer>> chunkLocal = transparentRunCache.computeIfAbsent(chunk, c -> new HashMap());
        ChunkLocalColumnPos columnOffset = new ChunkLocalColumnPos(pos);
        return chunkLocal.computeIfAbsent(columnOffset, o -> new TreeMap<Integer, Integer>());
    }

    protected NavigableMap<Integer, Integer> getTransparentRunCacheColumnIfExists(BlockPos pos) {
        LevelChunk chunk = level.getChunkAt(pos);
        Map<ChunkLocalColumnPos, NavigableMap<Integer, Integer>> chunkLocal = transparentRunCache.get(chunk);
        if (chunkLocal == null) {
            return null;
        }
        ChunkLocalColumnPos columnOffset = new ChunkLocalColumnPos(pos);
        return chunkLocal.get(columnOffset);
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
        invalidateTransparentRunCache(skyEngine, pos);
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
            int surfaceY = findSurfaceBelowCached(skyEngine, source.position);
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
                skyEngine.callEnqueueIncrease(packedSourcePos, ADD_TOP_ARTIFICIAL_SKY_SOURCE_ENTRY);
            }
            if (storage.callLightOnInSection(SectionPos.blockToSection(packedGuardPos)) && storage.get(packedGuardPos) >= MAX_LIGHT_LEVEL) {
                // The block just above was under real sky or another skylight source
                // We need to make sure that if it's no longer the case
                // Then the caused update will stop before it reaches the current source
                // Else it will be restored to the max level
                storage.set(packedGuardPos, MAX_LIGHT_LEVEL - 1);
            }
            skyEngine.callUpdateSourcesInColumn(x, z, surfaceY);
            if (surfaceY <= y && y < source.position.getY())  {
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
        return findSurfaceBelowWithLimit(skyEngine, topPos, minY);
    }

    protected int findSurfaceBelowWithLimit(SkyLightEngineAccessor skyEngine, BlockPos topPos, int minY) {
        BlockPos.MutableBlockPos prevPos = topPos.mutable();
        BlockPos.MutableBlockPos pos = topPos.mutable();
        BlockState prevBlockState = skyEngine.callGetState(prevPos);
        int y;
        for (y = topPos.getY() - 1; y >= minY; --y) {
            pos.setY(y);
            BlockState blockState = skyEngine.callGetState(pos);
            if (blockState.getLightBlock(level, pos) > 0) {
                break;
            }
            if (skyEngine.callShapeOccludes(prevPos.asLong(), prevBlockState, pos.asLong(), blockState, Direction.DOWN)) {
                break;
            }
            prevPos.setY(y);
            prevBlockState = blockState;
        }
        return y + 1;
    }

    protected int findSurfaceBelowCached(SkyLightEngineAccessor skyEngine, BlockPos topPos) {
        NavigableMap<Integer, Integer> cache = getTransparentRunCacheColumn(topPos);
        Integer topY = topPos.getY();
        Map.Entry<Integer, Integer> entry = cache.floorEntry(topY);  // The block at the topPos cannot be like a lower slab!
        if (entry == null) {
            // Below everything cached
            int result = findSurfaceBelow(skyEngine, topPos);
            cache.put(result, topY);
            return result;
        }
        int foundTopY = entry.getValue();
        if (foundTopY >= topY) {
            // We are inside a cached run
            return entry.getKey();
        }
        int result = findSurfaceBelowWithLimit(skyEngine, topPos, foundTopY);  // Stop the search when we hit a cached run
        if (result <= foundTopY) {
            // If we hit a cached run, we may increase its high element
            Integer key = entry.getKey();
            cache.put(key, topY);
            return key;
        }
        // Otherwise cache a new run
        cache.put(result, topY);
        return result;
    }

    protected void invalidateTransparentRunCache(SkyLightEngineAccessor skyEngine, BlockPos pos) {
        NavigableMap<Integer, Integer> cache = getTransparentRunCacheColumnIfExists(pos);
        if (cache == null) {
            return;
        }
        final int y = pos.getY();
        Map.Entry<Integer, Integer> entry = cache.floorEntry(y + 1);  // Prefer the top face entry
        if (entry == null) {
            // Below everything cached
            return;
        }
        if (entry.getValue() < y) {
            // Not inside a cached run
            return;
        }
        // Previous and next when going upside down
        BlockPos prevPos = pos.offset(0, 1, 0);
        BlockPos nextPos = pos.offset(0, -1, 0);
        BlockState prevState = skyEngine.callGetState(prevPos);
        BlockState state = skyEngine.callGetState(pos);
        BlockState nextState = skyEngine.callGetState(nextPos);
        boolean transparentTopFace = false;
        boolean transparentBottomFace = false;
        if (state.getLightBlock(level, pos) < 1) {
            transparentTopFace    = !skyEngine.callShapeOccludes(prevPos.asLong(), prevState, pos.asLong(), state, Direction.DOWN);
            transparentBottomFace = !skyEngine.callShapeOccludes(pos.asLong(), state, nextPos.asLong(), nextState, Direction.DOWN);
        }
        if (!transparentTopFace && !transparentBottomFace) {
            // Solid block
            Integer runBottom = entry.getKey();
            if (runBottom <= y) {
                Integer runTop = entry.getValue();
                if (runTop > y) {
                    // The block is just below the upper run
                    cache.put(y + 1, runTop);
                }
                if (runBottom == y) {
                    // No lower run
                    cache.remove(entry.getKey());
                } else {
                    // The block is just above the lower run
                    cache.put(runBottom, y);
                }
            } else {  // runBottom == y + 1
                Map.Entry<Integer, Integer> lowerEntry = cache.floorEntry(y);
                if (lowerEntry != null && lowerEntry.getValue() > y) {
                    // Convert from an upper slab
                    cache.put(lowerEntry.getKey(), y);
                }
            }
        } else if (!transparentTopFace && transparentBottomFace) {
            // Upper slab
            Integer runBottom = entry.getKey();
            if (runBottom <= y) {
                Integer runTop = entry.getValue();
                if (runTop > y) {
                    // The block is just below the upper run
                    cache.put(y + 1, runTop);
                }
                cache.put(runBottom, y + 1);  // The slab is a part of the lower run
            } else {  // runBottom == y + 1
                Map.Entry<Integer, Integer> lowerEntry = cache.floorEntry(y);
                if (lowerEntry != null && lowerEntry.getValue() == y) {
                    // Convert from a solid block
                    cache.put(lowerEntry.getKey(), y + 1);
                }
            }
        } else if (transparentTopFace && !transparentBottomFace) {
            // Lower slab
            Integer runBottom = entry.getKey();
            if (runBottom <= y) {
                Integer runTop = entry.getValue();
                if (runTop > y) {
                    // The block is the lowest in the upper run
                    cache.put(y, runTop);
                }
                if (runBottom < y) {
                    // The block is just above the lower run
                    cache.put(runBottom, y);
                }
                if (runTop == (int) runBottom) {
                    cache.remove(runBottom);
                }
            } else {  // runBottom == y + 1
                // Include the slab into the upper run
                cache.put(y, entry.getValue());
                cache.remove(y + 1);
            }
        } else {
            // Fully transparent (air/glass)
            Integer runBottom = entry.getKey();
            if (runBottom >= y) {
                // The block just below the upper run was broken
                int newBottom = findSurfaceBelowCached(skyEngine, pos);
                cache.put(newBottom, entry.getValue());
                cache.remove(runBottom);
            }
        }
    }
}
