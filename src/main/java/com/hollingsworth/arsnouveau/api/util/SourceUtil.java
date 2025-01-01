package com.hollingsworth.arsnouveau.api.util;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.hollingsworth.arsnouveau.api.source.*;
import com.hollingsworth.arsnouveau.common.block.tile.CreativeSourceJarTile;
import com.hollingsworth.arsnouveau.common.block.tile.SourceJarTile;
import com.hollingsworth.arsnouveau.common.entity.EntityFollowProjectile;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.*;

public class SourceUtil {

    public static List<ISpecialSourceProvider> canGiveSource(BlockPos pos, Level world, int range) {
        List<ISpecialSourceProvider> posList = new ArrayList<>();
        BlockPos.withinManhattanStream(pos, range, range, range).forEach(b -> {
            if (world.isLoaded(b) && world.getBlockEntity(b) instanceof SourceJarTile jar && jar.canAcceptSource())
                posList.add(new SourceProvider(jar, b.immutable()));
        });
        List<ISpecialSourceProvider> provider = SourceManager.INSTANCE.canGiveSourceNearby(pos, world, range);
        for(ISpecialSourceProvider p : provider){
            posList.add(new SourceProvider(p));
        }
        return posList;
    }

    public static List<ISpecialSourceProvider> canTakeSource(BlockPos pos, Level world, int range) {
        List<ISpecialSourceProvider> posList = new ArrayList<>();
        BlockPos.withinManhattanStream(pos, range, range, range).forEach(b -> {
            if (world.isLoaded(b) && world.getBlockEntity(b) instanceof SourceJarTile jar && jar.getSource() > 0)
                posList.add(new SourceProvider(jar, b.immutable()));
        });
        List<ISpecialSourceProvider> provider = SourceManager.INSTANCE.canTakeSourceNearby(pos, world, range);
        for(ISpecialSourceProvider p : provider){
            posList.add(new SourceProvider(p));
        }
        return posList;
    }

    /**
     * @deprecated Use {@link SourceUtil#takeSourceMultiple}
     * @param pos Position around which to find source providers
     * @param level Level to find source providers in
     * @param range Range to check around `pos`
     * @param source How much source to extract
     * @return Provider that was extracted from, or null if none had enough source.
     */
    @Deprecated(forRemoval = true)
    public static @Nullable ISpecialSourceProvider takeSource(BlockPos pos, Level level, int range, int source){
        List<ISpecialSourceProvider> providers = canTakeSource(pos, level, range);
        for(ISpecialSourceProvider provider : providers){
            if(provider.getSource().getSource() >= source){
                provider.getSource().removeSource(source);
                return provider;
            }
        }
        return null;
    }

    /**
     * @param pos Position around which to find source providers
     * @param level Level to find source providers in
     * @param range Range to check around `pos`
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

            int initial = sourceTile.getSource();
            int available = Math.min(needed, initial);
            int after = sourceTile.removeSource(available);
            if (needed > 0 && initial > after) {
                int extracted = initial - after;
                needed -= extracted;
                takenFrom.put(provider, extracted);
            }

            // We can't break even if needed < 0 as there may still be a Creative Source Jar in the list
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
     * @deprecated Use {@link SourceUtil#takeSourceMultipleWithParticles(BlockPos, Level, int, int)}
     * @param pos Position around which to find source providers
     * @param level Level to find source providers in
     * @param range Range to check around `pos`
     * @param source How much source to extract
     * @return Provider that was extracted from, or null if none had enough source.
     */
    @Deprecated(forRemoval = true)
    public static @Nullable ISpecialSourceProvider takeSourceWithParticles(BlockPos pos, Level level, int range, int source){
        return takeSourceWithParticles(pos, pos, level, range, source);
    }

    /**
     * @param pos Position around which to find source providers
     * @param level Level to find source providers in
     * @param range Range to check around `pos`
     * @param source How much source to extract
     * @return List of all the providers extracted from, or null if there was not enough total source.
     */
    public static @Nullable List<ISpecialSourceProvider> takeSourceMultipleWithParticles(BlockPos pos, Level level, int range, int source){
        return takeSourceMultipleWithParticles(pos, pos, level, range, source);
    }

    /**
     * @deprecated Use {@link SourceUtil#takeSourceMultipleWithParticles(BlockPos, BlockPos, Level, int, int)}
     * @param pos Position around which to find source providers
     * @param level Level to find source providers in
     * @param range Range to check around `pos`
     * @param source How much source to extract
     * @return Provider that was extracted from, or null if none had enough source.
     */
    @Deprecated(forRemoval = true)
    public static @Nullable ISpecialSourceProvider takeSourceWithParticles(BlockPos pos, BlockPos particlesTo, Level level, int range, int source){
        ISpecialSourceProvider result = takeSource(pos, level, range, source);
        if(result != null && level instanceof ServerLevel serverLevel){
            EntityFollowProjectile.spawn(serverLevel, result.getCurrentPos(), particlesTo);
        }
        return result;
    }

    /**
     * @param pos Position around which to find source providers
     * @param level Level to find source providers in
     * @param range Range to check around `pos`
     * @param source How much source to extract
     * @return List of all the providers extracted from, or null if there was not enough total source.
     */
    public static @Nullable List<ISpecialSourceProvider> takeSourceMultipleWithParticles(BlockPos pos, BlockPos particlesTo, Level level, int range, int source){
        List<ISpecialSourceProvider> result = takeSourceMultiple(pos, level, range, source);
        if(result != null && level instanceof ServerLevel serverLevel){
            for (ISpecialSourceProvider provider : result) {
                EntityFollowProjectile.spawn(serverLevel, provider.getCurrentPos(), particlesTo);
            }
        }
        return result;
    }

    /**
     * Searches for nearby mana jars that have enough mana.
     * Returns the position where the source was taken, or null if none were found.
     */
    public static boolean hasSourceNearby(BlockPos pos, Level world, int range, int source) {
        Optional<BlockPos> loc = BlockPos.findClosestMatch(pos, range, range, (b) -> world.getBlockEntity(b) instanceof SourceJarTile jar && jar.getSource() >= source);
        if(loc.isPresent()){
            return true;
        }
        return SourceManager.INSTANCE.hasSourceNearby(pos, world, range, source) != null;
    }
}
