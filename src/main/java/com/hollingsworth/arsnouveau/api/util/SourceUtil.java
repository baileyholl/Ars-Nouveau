package com.hollingsworth.arsnouveau.api.util;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.source.ISpecialSourceProvider;
import com.hollingsworth.arsnouveau.api.source.SourceManager;
import com.hollingsworth.arsnouveau.api.source.SourceProvider;
import com.hollingsworth.arsnouveau.common.block.tile.SourceJarTile;
import com.hollingsworth.arsnouveau.common.entity.EntityFollowProjectile;
import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mod.EventBusSubscriber(modid = ArsNouveau.MODID)
public class SourceUtil {

    public static List<ISpecialSourceProvider> canGiveSource(BlockPos pos, Level world, int range) {
        List<ISpecialSourceProvider> posList = new ArrayList<>();
        BlockPos.withinManhattanStream(pos, range, range, range).forEach(b -> {
            if (world.isLoaded(b)) {
                Direction dir = getDirTo(pos, b);
                BlockEntity be = world.getBlockEntity(b);

                if (be != null) {
                    be.getCapability(CapabilityRegistry.SOURCE_TILE, dir).ifPresent(sourceTile -> {
                        if (sourceTile.canAcceptSource() && sourceTile.sourcelinksCanProvideSource())
                            posList.add(new SourceProvider(sourceTile, b.immutable(), dir));
                    });
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
                Direction dir = getDirTo(pos, b);
                BlockEntity be = world.getBlockEntity(b);

                if (be != null) {
                    be.getCapability(CapabilityRegistry.SOURCE_TILE, dir).ifPresent(sourceTile -> {
                        if (sourceTile.getSource() > 0 && sourceTile.machinesCanTakeSource())
                            posList.add(new SourceProvider(sourceTile, b.immutable(), dir));
                    });
                }
            }
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
        Optional<BlockPos> loc = BlockPos.findClosestMatch(pos, range, range, (b) -> {
            BlockEntity be = world.getBlockEntity(b);

            return be != null && be.getCapability(CapabilityRegistry.SOURCE_TILE, getDirTo(pos, b))
                    .filter(tile -> tile.getSource() >= source && tile.machinesCanTakeSource())
                    .isPresent();
        });
        if(loc.isPresent()){
            return true;
        }
        return SourceManager.INSTANCE.hasSourceNearby(pos, world, range, source) != null;
    }

    public static Direction getDirTo(BlockPos from, BlockPos to) {
        var x = from.getX() - to.getX();
        var y = from.getY() - to.getY();
        var z = from.getZ() - to.getZ();

        if (Math.abs(y) >= Math.abs(x) && Math.abs(y) >= Math.abs(z)) {
            return y > 0 ? Direction.UP : Direction.DOWN;
        } else if (Math.abs(x) > Math.abs(z)) {
            return x > 0 ? Direction.EAST : Direction.WEST;
        } else {
            return z > 0 ? Direction.SOUTH : Direction.NORTH;
        }
    }
}
