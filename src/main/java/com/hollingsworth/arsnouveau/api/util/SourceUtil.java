package com.hollingsworth.arsnouveau.api.util;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.hollingsworth.arsnouveau.api.source.ISourceCap;
import com.hollingsworth.arsnouveau.api.source.ISpecialSourceProvider;
import com.hollingsworth.arsnouveau.api.source.SourceManager;
import com.hollingsworth.arsnouveau.api.source.SourceProvider;
import com.hollingsworth.arsnouveau.common.block.CreativeSourceJar;
import com.hollingsworth.arsnouveau.common.block.tile.CreativeSourceJarTile;
import com.hollingsworth.arsnouveau.common.capability.SourceStorage;
import com.hollingsworth.arsnouveau.common.entity.EntityFollowProjectile;
import com.hollingsworth.arsnouveau.common.util.Log;
import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.*;

public class SourceUtil {

    public static List<ISpecialSourceProvider> canGiveSource(BlockPos pos, Level world, int range) {
        List<ISpecialSourceProvider> posList = new ArrayList<>();
        BlockPos.withinManhattanStream(pos, range, range, range).forEach(b -> {
            if (world.isLoaded(b)) {
                ISourceCap cap = world.getCapability(CapabilityRegistry.SOURCE_CAPABILITY, b, null);
                if (cap != null && cap.canAcceptSource(1)) {
                    posList.add(new SourceProvider(cap, b.immutable()));
                }
            }
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
            if (world.isLoaded(b)) {
                ISourceCap cap = world.getCapability(CapabilityRegistry.SOURCE_CAPABILITY, b, null);
                if (cap != null && cap.canProvideSource(1)) {
                    posList.add(new SourceProvider(cap, b.immutable()));
                }
            }
        });
        List<ISpecialSourceProvider> provider = SourceManager.INSTANCE.canTakeSourceNearby(pos, world, range);
        for(ISpecialSourceProvider p : provider){
            posList.add(new SourceProvider(p));
        }
        return posList;
    }

    /**
     * @param pos Position around which to find source providers
     * @param level Level to find source providers in
     * @param range Range to check around `pos`
     * @param source How much source to extract
     * @return List of all the providers extracted from, or null if there was not enough total source.
     */
    public static @Nullable List<ISpecialSourceProvider> takeSource(BlockPos pos, Level level, int range, int source) {
        List<ISpecialSourceProvider> providers = canTakeSource(pos, level, range);
        Multimap<ISpecialSourceProvider, Integer> potentialRefunds = Multimaps.newMultimap(new HashMap<>(), ArrayList::new);

        int needed = source;
        for (ISpecialSourceProvider provider : providers) {
            ISourceCap cap = provider.getCapability();
            if (cap instanceof CreativeSourceJarTile.InfiniteSourceStorage) {
                for (Map.Entry<ISpecialSourceProvider, Integer> entry : potentialRefunds.entries()) {
                    entry.getKey().getCapability().receiveSource(entry.getValue(), false);
                }

                return List.of(provider);
            }

            int available = Math.min(needed, cap.getSource());
            int extracted = cap.extractSource(available, false);
            if (needed > 0 && extracted > 0) {
                needed -= extracted;
                potentialRefunds.put(provider, extracted);
            }

            if (needed <= 0) {
                break;
            }
        }

        if (needed > 0) {
            for (Map.Entry<ISpecialSourceProvider, Integer> entry : potentialRefunds.entries()) {
                entry.getKey().getCapability().receiveSource(entry.getValue(), false);
            }
            return null;
        }

        return new ArrayList<>(potentialRefunds.keys());
    }

    /**
     * @param pos Position around which to find source providers
     * @param level Level to find source providers in
     * @param range Range to check around `pos`
     * @param source How much source to extract
     * @return List of all the providers extracted from, or null if there was not enough total source.
     */
    public static @Nullable List<ISpecialSourceProvider> takeSourceWithParticles(BlockPos pos, Level level, int range, int source){
        return takeSourceWithParticles(pos, pos, level, range, source);
    }

    /**
     * @param pos Position around which to find source providers
     * @param particlesTo Position spawned particles should travel towards
     * @param level Level to find source providers in
     * @param range Range to check around `pos`
     * @param source How much source to extract
     * @return List of all the providers extracted from, or null if there was not enough total source.
     */
    public static @Nullable List<ISpecialSourceProvider> takeSourceWithParticles(BlockPos pos, BlockPos particlesTo, Level level, int range, int source){
        List<ISpecialSourceProvider> result = takeSource(pos, level, range, source);

        if (result != null && level instanceof ServerLevel serverLevel) {
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
        Optional<BlockPos> loc = BlockPos.findClosestMatch(pos, range, range, (b) -> {
            ISourceCap cap = world.getCapability(CapabilityRegistry.SOURCE_CAPABILITY, b, null);
            return cap != null && cap.canProvideSource(source);
        });
        if(loc.isPresent()){
            return true;
        }
        return SourceManager.INSTANCE.hasSourceNearby(pos, world, range, source) != null;
    }
}
