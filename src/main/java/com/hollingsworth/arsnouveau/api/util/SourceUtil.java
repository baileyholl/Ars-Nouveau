package com.hollingsworth.arsnouveau.api.util;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.source.ISpecialSourceProvider;
import com.hollingsworth.arsnouveau.api.source.SourceManager;
import com.hollingsworth.arsnouveau.api.source.SourceProvider;
import com.hollingsworth.arsnouveau.common.block.tile.SourceJarTile;
import com.hollingsworth.arsnouveau.common.entity.EntityFollowProjectile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.neoforged.fml.common.EventBusSubscriber;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@EventBusSubscriber(modid = ArsNouveau.MODID)
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

    public static @Nullable ISpecialSourceProvider takeSourceWithParticles(BlockPos pos, Level level, int range, int source){
        ISpecialSourceProvider result = takeSource(pos, level, range, source);
        if(result != null){
            EntityFollowProjectile aoeProjectile = new EntityFollowProjectile(level, result.getCurrentPos(), pos);
            level.addFreshEntity(aoeProjectile);
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
