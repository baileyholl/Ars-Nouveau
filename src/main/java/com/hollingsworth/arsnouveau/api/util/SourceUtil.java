package com.hollingsworth.arsnouveau.api.util;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.hollingsworth.arsnouveau.api.source.ISourceCap;
import com.hollingsworth.arsnouveau.api.source.ISourceTile;
import com.hollingsworth.arsnouveau.api.source.ISpecialSourceProvider;
import com.hollingsworth.arsnouveau.api.source.SourceAccess;
import com.hollingsworth.arsnouveau.api.source.SourceManager;
import com.hollingsworth.arsnouveau.api.source.SourceProvider;
import com.hollingsworth.arsnouveau.common.datagen.BlockTagProvider;
import com.hollingsworth.arsnouveau.common.entity.EntityFollowProjectile;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SourceUtil {

    /**
     * Finds every block capable of providing source within {@code range} of {@code pos}, excluding {@code pos} itself.
     *
     * @param pos   Position around which to find source providers
     * @param level Level to find source providers in
     * @param range Range to check around `pos`
     * @return List of all the providers found, in order.
     */
    public static List<SourceAccess> findProviders(BlockPos pos, Level level, int range) {
        List<SourceAccess> providers = new ArrayList<>();
        for (SourceAccess access : findNearby(pos, level, range, SourceManager.INSTANCE.canTakeSourceNearby(pos, level, range))) {
            if (access.cap().canProvideSource(1)) {
                providers.add(access);
            }
        }
        return providers;
    }

    /**
     * Finds every block capable of accepting source within {@code range} of {@code pos}, excluding {@code pos} itself.
     *
     * @param pos   Position around which to find source stores
     * @param level Level to find source stores in
     * @param range Range to check around `pos`
     * @return List of all the stores found, in order.
     */
    public static List<SourceAccess> findAcceptors(BlockPos pos, Level level, int range) {
        List<SourceAccess> acceptors = new ArrayList<>();
        for (SourceAccess access : findNearby(pos, level, range, SourceManager.INSTANCE.canGiveSourceNearby(pos, level, range))) {
            if (!access.cap().isInfinite() && access.cap().canAcceptSource(1)) {
                acceptors.add(access);
            }
        }
        return acceptors;
    }

    private static List<SourceAccess> findNearby(BlockPos pos, Level level, int range, List<ISpecialSourceProvider> providers) {
        List<SourceAccess> found = new ArrayList<>();
        Set<BlockPos> blockPositions = new HashSet<>();
        Set<ISourceCap> caps = Collections.newSetFromMap(new IdentityHashMap<>());
        for (BlockPos b : BlockPos.withinManhattan(pos, range, range, range)) {
            if (b.equals(pos)) {
                continue;
            }
            SourceAccess access = SourceAccess.at(level, b, null);
            if (access == null || level.getBlockState(access.pos()).is(BlockTagProvider.SOURCE_SCAN_EXCLUDED)) {
                continue;
            }
            blockPositions.add(access.pos());
            if (caps.add(access.cap())) {
                found.add(access);
            }
        }
        for (ISpecialSourceProvider provider : providers) {
            BlockPos providerPos = provider.getCurrentPos();
            if (!providerPos.equals(pos) && !blockPositions.contains(providerPos)) {
                found.add(new SourceAccess(providerPos, new TileSourceCap(provider.getSource())));
            }
        }
        return found;
    }

    /**
     * Takes exactly {@code amount} source from all stores found by {@link SourceUtil#findProviders}
     * If any infinite provider is found, everything will be taken from that provider.
     * If all stores together do not have enough source, nothing will be extracted.
     *
     * @param pos    Position around which to find source stores
     * @param level  Level to find source stores in
     * @param range  Range to check around `pos`
     * @param amount How much source to extract
     * @return List of all the stores extracted from, or null if there was not enough total source.
     */
    public static @Nullable List<SourceAccess> takeNearby(BlockPos pos, Level level, int range, int amount) {
        List<SourceAccess> providers = findProviders(pos, level, range);
        for (SourceAccess access : providers) {
            if (access.cap().isInfinite()) {
                return List.of(access);
            }
        }

        Map<SourceAccess, Integer> sourceAmounts = new LinkedHashMap<>();
        int needed = amount;
        for (SourceAccess access : providers) {
            if (needed <= 0) {
                break;
            }
            
            int taken = access.cap().extractSource(needed, true);
            if (taken > 0) {
                sourceAmounts.put(access, taken);
                needed -= taken;
            }
        }

        if (needed > 0) {
            return null;
        }

        for (Map.Entry<SourceAccess, Integer> charge : sourceAmounts.entrySet()) {
            charge.getKey().cap().extractSource(charge.getValue(), false);
        }
        
        return List.copyOf(sourceAmounts.keySet());
    }

    /**
     * Same as {@link SourceUtil#takeNearby}, plus a follow projectile from each store extracted moving to {@code pos}.
     *
     * @param pos    Position around which to find source stores
     * @param level  Level to find source stores in
     * @param range  Range to check around `pos`
     * @param amount How much source to extract
     * @return List of all the stores extracted from, or null if there was not enough total source.
     */
    public static @Nullable List<SourceAccess> takeNearbyWithParticles(BlockPos pos, Level level, int range, int amount) {
        return takeNearbyWithParticles(pos, pos, level, range, amount);
    }

    /**
     * Same as {@link SourceUtil#takeNearby} and spawns a follow projectile from each store extracted moving to {@code particlesTo}.
     *
     * @param pos         Position around which to find source stores
     * @param particlesTo Position the projectiles fly to
     * @param level       Level to find source stores in
     * @param range       Range to check around {@code pos}
     * @param amount      How much source to extract
     * @return List of all the stores extracted from, or null if there was not enough total source.
     */
    public static @Nullable List<SourceAccess> takeNearbyWithParticles(BlockPos pos, BlockPos particlesTo, Level level, int range, int amount) {
        List<SourceAccess> sources = takeNearby(pos, level, range, amount);
        if (sources != null && level instanceof ServerLevel serverLevel) {
            for (SourceAccess access : sources) {
                EntityFollowProjectile.spawn(serverLevel, access.pos(), particlesTo);
            }
        }
        return sources;
    }

    /**
     * @deprecated Use {@link SourceUtil#findAcceptors(BlockPos, Level, int)}.
     */
    @Deprecated(forRemoval = true)
    public static List<ISpecialSourceProvider> canGiveSource(BlockPos pos, Level world, int range) {
        List<ISpecialSourceProvider> providers = new ArrayList<>();
        for (SourceAccess access : findAcceptors(pos, world, range)) {
            providers.add(asProvider(access, world));
        }
        return providers;
    }

    /**
     * @deprecated Use {@link SourceUtil#findProviders(BlockPos, Level, int)}.
     */
    @Deprecated(forRemoval = true)
    public static List<ISpecialSourceProvider> canTakeSource(BlockPos pos, Level world, int range) {
        List<ISpecialSourceProvider> providers = new ArrayList<>();
        for (SourceAccess access : findProviders(pos, world, range)) {
            providers.add(asProvider(access, world));
        }
        return providers;
    }

    private static ISpecialSourceProvider asProvider(SourceAccess access, Level level) {
        if (access.cap() instanceof TileSourceCap(ISourceTile tile)) {
            return new SourceProvider(tile, access.pos());
        }
        if (level.getBlockEntity(access.pos()) instanceof ISourceTile tile) {
            return new SourceProvider(tile, access.pos());
        }
        return new SourceProvider(new SourceTileCap(access.cap()), access.pos());
    }

    /**
     * @param pos    Position around which to find source providers
     * @param level  Level to find source providers in
     * @param range  Range to check around `pos`
     * @param source How much source to extract
     * @return Provider that was extracted from, or null if none had enough source.
     *
     * @deprecated Use {@link SourceUtil#takeNearby(BlockPos, Level, int, int)}.
     */
    @Deprecated(forRemoval = true)
    public static @Nullable ISpecialSourceProvider takeSource(BlockPos pos, Level level, int range, int source) {
        List<ISpecialSourceProvider> providers = canTakeSource(pos, level, range);
        for (ISpecialSourceProvider provider : providers) {
            if (provider.getSource().getSource() >= source) {
                provider.getSource().removeSource(source);
                return provider;
            }
        }
        return null;
    }

    /**
     * @param pos    Position around which to find source providers
     * @param level  Level to find source providers in
     * @param range  Range to check around `pos`
     * @param source How much source to extract
     * @return List of all the providers extracted from, or null if there was not enough total source.
     *
     * @deprecated Use {@link SourceUtil#takeNearby(BlockPos, Level, int, int)}.
     */
    @Deprecated(forRemoval = true)
    public static @Nullable List<ISpecialSourceProvider> takeSourceMultiple(BlockPos pos, Level level, int range, int source) {
        List<ISpecialSourceProvider> providers = canTakeSource(pos, level, range);
        Multimap<ISpecialSourceProvider, Integer> takenFrom = Multimaps.newMultimap(new HashMap<>(), ArrayList::new);

        int needed = source;
        for (ISpecialSourceProvider provider : providers) {
            ISourceTile sourceTile = provider.getSource();
            if (sourceTile.isInfinite()) {
                for (Map.Entry<ISpecialSourceProvider, Integer> entry : takenFrom.entries()) {
                    entry.getKey().getSource().addSource(entry.getValue());
                }

                return List.of(provider);
            }

            if (needed <= 0) {
                continue;
            }

            int initial = sourceTile.getSource();
            int available = Math.min(needed, initial);
            int after = sourceTile.removeSource(available);
            if (initial > after) {
                int extracted = initial - after;
                needed -= extracted;
                takenFrom.put(provider, extracted);
            }

            // We can't break even if needed <= 0 as there may still be a Creative Source Jar in the list
        }

        if (needed > 0) {
            for (Map.Entry<ISpecialSourceProvider, Integer> entry : takenFrom.entries()) {
                entry.getKey().getSource().addSource(entry.getValue());
            }
            return null;
        }

        return new ArrayList<>(takenFrom.keys());
    }

    /**
     * @param pos    Position around which to find source providers
     * @param level  Level to find source providers in
     * @param range  Range to check around `pos`
     * @param source How much source to extract
     * @return Provider that was extracted from, or null if none had enough source.
     *
     * @deprecated Use {@link SourceUtil#takeNearbyWithParticles(BlockPos, Level, int, int)}.
     */
    @Deprecated(forRemoval = true)
    public static @Nullable ISpecialSourceProvider takeSourceWithParticles(BlockPos pos, Level level, int range, int source) {
        return takeSourceWithParticles(pos, pos, level, range, source);
    }

    /**
     * @param pos    Position around which to find source providers
     * @param level  Level to find source providers in
     * @param range  Range to check around `pos`
     * @param source How much source to extract
     * @return List of all the providers extracted from, or null if there was not enough total source.
     *
     * @deprecated Use {@link SourceUtil#takeNearbyWithParticles(BlockPos, Level, int, int)}.
     */
    @Deprecated(forRemoval = true)
    public static @Nullable List<ISpecialSourceProvider> takeSourceMultipleWithParticles(BlockPos pos, Level level, int range, int source) {
        return takeSourceMultipleWithParticles(pos, pos, level, range, source);
    }

    /**
     * @param pos    Position around which to find source providers
     * @param level  Level to find source providers in
     * @param range  Range to check around `pos`
     * @param source How much source to extract
     * @return Provider that was extracted from, or null if none had enough source.
     *
     * @deprecated Use {@link SourceUtil#takeNearbyWithParticles(BlockPos, BlockPos, Level, int, int)}.
     */
    @Deprecated(forRemoval = true)
    public static @Nullable ISpecialSourceProvider takeSourceWithParticles(BlockPos pos, BlockPos particlesTo, Level level, int range, int source) {
        ISpecialSourceProvider result = takeSource(pos, level, range, source);
        if (result != null && level instanceof ServerLevel serverLevel) {
            EntityFollowProjectile.spawn(serverLevel, result.getCurrentPos(), particlesTo);
        }
        return result;
    }

    /**
     * @param pos    Position around which to find source providers
     * @param level  Level to find source providers in
     * @param range  Range to check around `pos`
     * @param source How much source to extract
     * @return List of all the providers extracted from, or null if there was not enough total source.
     *
     * @deprecated Use {@link SourceUtil#takeNearbyWithParticles(BlockPos, BlockPos, Level, int, int)}.
     */
    @Deprecated(forRemoval = true)
    public static @Nullable List<ISpecialSourceProvider> takeSourceMultipleWithParticles(BlockPos pos, BlockPos particlesTo, Level level, int range, int source) {
        List<ISpecialSourceProvider> result = takeSourceMultiple(pos, level, range, source);
        if (result != null && level instanceof ServerLevel serverLevel) {
            for (ISpecialSourceProvider provider : result) {
                EntityFollowProjectile.spawn(serverLevel, provider.getCurrentPos(), particlesTo);
            }
        }
        return result;
    }

    /**
     * Searches for source in nearby source jars.
     * Returns whether enough source was found.
     */
    public static boolean hasSourceNearby(BlockPos pos, Level world, int range, int source) {
        for (var provider : SourceUtil.canTakeSource(pos, world, range)) {
            ISourceTile sourceTile = provider.getSource();
            if (sourceTile.isInfinite()) {
                return true;
            }

            source -= sourceTile.removeSource(source, true);
            if (source <= 0) {
                return true;
            }
        }

        return false;
    }
}
