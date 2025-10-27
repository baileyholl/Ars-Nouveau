package com.hollingsworth.arsnouveau.api.util;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.hollingsworth.arsnouveau.api.source.ISourceTile;
import com.hollingsworth.arsnouveau.api.source.ISpecialSourceProvider;
import com.hollingsworth.arsnouveau.api.source.SourceManager;
import com.hollingsworth.arsnouveau.api.source.SourceProvider;
import com.hollingsworth.arsnouveau.common.block.tile.CreativeSourceJarTile;
import com.hollingsworth.arsnouveau.common.block.tile.SourceJarTile;
import com.hollingsworth.arsnouveau.common.entity.EntityFollowProjectile;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SourceUtil {

    public static List<ISpecialSourceProvider> canGiveSource(BlockPos pos, Level world, int range) {
        List<ISpecialSourceProvider> posList = new ArrayList<>();
        for (BlockPos b : BlockPos.withinManhattan(pos, range, range, range)) {
            if (world.isLoaded(b) && world.getBlockEntity(b) instanceof SourceJarTile jar && jar.canAcceptSource())
                posList.add(new SourceProvider(jar, b.immutable()));
        }
        List<ISpecialSourceProvider> provider = SourceManager.INSTANCE.canGiveSourceNearby(pos, world, range);
        for (ISpecialSourceProvider p : provider) {
            posList.add(new SourceProvider(p));
        }
        return posList;
    }

    public static List<ISpecialSourceProvider> canTakeSource(BlockPos pos, Level world, int range) {
        List<ISpecialSourceProvider> posList = new ArrayList<>();
        for (BlockPos b : BlockPos.withinManhattan(pos, range, range, range)) {
            if (world.isLoaded(b) && world.getBlockEntity(b) instanceof SourceJarTile jar && jar.getSource() > 0)
                posList.add(new SourceProvider(jar, b.immutable()));
        }
        List<ISpecialSourceProvider> provider = SourceManager.INSTANCE.canTakeSourceNearby(pos, world, range);
        for (ISpecialSourceProvider p : provider) {
            posList.add(new SourceProvider(p));
        }
        return posList;
    }

    /**
     * @param pos    Position around which to find source providers
     * @param level  Level to find source providers in
     * @param range  Range to check around `pos`
     * @param source How much source to extract
     * @return Provider that was extracted from, or null if none had enough source.
     * @deprecated Use {@link SourceUtil#takeSourceMultiple}
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
     */
    public static @Nullable List<ISpecialSourceProvider> takeSourceMultiple(BlockPos pos, Level level, int range, int source) {
        List<ISpecialSourceProvider> providers = canTakeSource(pos, level, range);
        Multimap<ISpecialSourceProvider, Integer> takenFrom = Multimaps.newMultimap(new HashMap<>(), ArrayList::new);

        int needed = source;
        for (ISpecialSourceProvider provider : providers) {
            ISourceTile sourceTile = provider.getSource();
            if (sourceTile instanceof CreativeSourceJarTile) {
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
     * @deprecated Use {@link SourceUtil#takeSourceMultipleWithParticles(BlockPos, Level, int, int)}
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
     */
    public static @Nullable List<ISpecialSourceProvider> takeSourceMultipleWithParticles(BlockPos pos, Level level, int range, int source) {
        return takeSourceMultipleWithParticles(pos, pos, level, range, source);
    }

    /**
     * @param pos    Position around which to find source providers
     * @param level  Level to find source providers in
     * @param range  Range to check around `pos`
     * @param source How much source to extract
     * @return Provider that was extracted from, or null if none had enough source.
     * @deprecated Use {@link SourceUtil#takeSourceMultipleWithParticles(BlockPos, BlockPos, Level, int, int)}
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
     */
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
            if (sourceTile instanceof CreativeSourceJarTile) {
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
