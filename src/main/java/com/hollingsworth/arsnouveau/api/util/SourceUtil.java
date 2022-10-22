package com.hollingsworth.arsnouveau.api.util;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.source.ISpecialSourceProvider;
import com.hollingsworth.arsnouveau.api.source.SourceManager;
import com.hollingsworth.arsnouveau.api.source.SourceProvider;
import com.hollingsworth.arsnouveau.common.block.tile.SourceJarTile;
import com.hollingsworth.arsnouveau.common.entity.EntityFollowProjectile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mod.EventBusSubscriber(modid = ArsNouveau.MODID)
public class SourceUtil {

    /**
     * Searches for nearby mana jars that have enough mana.
     * Returns the position where the mana was taken, or null if none were found.
     */
    @Nullable
    public static BlockPos takeSourceNearby(BlockPos pos, Level world, int range, int mana) {
        Optional<BlockPos> loc = BlockPos.findClosestMatch(pos, range, range, (b) -> world.getBlockEntity(b) instanceof SourceJarTile && ((SourceJarTile) world.getBlockEntity(b)).getSource() >= mana);
        if (loc.isEmpty()) {
            ISpecialSourceProvider specialSourceProvider = SourceManager.INSTANCE.takeSourceNearby(pos, world, range, mana);
            return specialSourceProvider == null ? null : specialSourceProvider.getCurrentPos();
        }
        if (world.getBlockEntity(loc.get()) instanceof SourceJarTile tile)
            tile.removeSource(mana);
        return loc.get();
    }

    public static @Nullable BlockPos takeSourceNearbyWithParticles(BlockPos pos, Level world, int range, int mana) {
        BlockPos result = takeSourceNearby(pos, world, range, mana);
        if (result != null) {
            EntityFollowProjectile aoeProjectile = new EntityFollowProjectile(world, result, pos);
            world.addFreshEntity(aoeProjectile);
        }
        return result;
    }

    /**
     * Searches for nearby mana jars that have enough mana.
     * Returns the position where the mana was taken, or null if none were found.
     */
    public static boolean hasSourceNearby(BlockPos pos, Level world, int range, int mana) {
        Optional<BlockPos> loc = BlockPos.findClosestMatch(pos, range, range, (b) -> world.getBlockEntity(b) instanceof SourceJarTile jar && jar.getSource() >= mana);
        if(loc.isEmpty()){
            return SourceManager.INSTANCE.hasSourceNearby(pos, world, range, mana) != null;
        }
        return true;
    }

    @Nullable
    public static BlockPos canGiveSourceClosest(BlockPos pos, Level world, int range) {
        Optional<BlockPos> loc = BlockPos.findClosestMatch(pos, range, range, (b) -> world.getBlockEntity(b) instanceof SourceJarTile jar && jar.canAcceptSource());
        if(loc.isEmpty()){
            List<ISpecialSourceProvider> provider = SourceManager.INSTANCE.canGiveSourceNearby(pos, world, range);
            return provider.isEmpty() ? null : provider.get(0).getCurrentPos();
        }
        return loc.orElse(null);
    }

    public static List<SourceProvider> canGiveSource(BlockPos pos, Level world, int range) {
        List<SourceProvider> posList = new ArrayList<>();
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

    public static List<SourceProvider> canTakeSource(BlockPos pos, Level world, int range) {
        List<SourceProvider> posList = new ArrayList<>();
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

    // TODO: Delete in 1.19.3
    @Deprecated(forRemoval = true)
    public static List<BlockPos> canGiveSourceAny(BlockPos pos, Level world, int range) {
        List<BlockPos> posList = new ArrayList<>();
        BlockPos.withinManhattanStream(pos, range, range, range).forEach(b -> {
            if (world.getBlockEntity(b) instanceof SourceJarTile jar && jar.canAcceptSource())
                posList.add(b.immutable());
        });
        return posList;
    }

    // TODO: delete in 1.19.3
    @Deprecated(forRemoval = true)
    public static List<BlockPos> canTakeSourceAny(BlockPos pos, Level world, int range) {
        List<BlockPos> posList = new ArrayList<>();
        BlockPos.withinManhattanStream(pos, range, range, range).forEach(b -> {
            if (world.getBlockEntity(b) instanceof SourceJarTile jar && jar.getSource() > 0) {
                posList.add(b.immutable());
            }
        });
        return posList;
    }

}
