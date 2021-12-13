package com.hollingsworth.arsnouveau.api.util;

import com.hollingsworth.arsnouveau.common.block.tile.SourceJarTile;
import com.hollingsworth.arsnouveau.common.entity.EntityFollowProjectile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SourceUtil {

    /**
     * Searches for nearby mana jars that have enough mana.
     * Returns the position where the mana was taken, or null if none were found.
     */
    @Nullable
    public static BlockPos takeSourceNearby(BlockPos pos, Level world, int range, int mana){
        Optional<BlockPos> loc = BlockPos.findClosestMatch(pos, range, range, (b) -> world.getBlockEntity(b) instanceof SourceJarTile && ((SourceJarTile) world.getBlockEntity(b)).getSource() >= mana);
        if(!loc.isPresent())
            return null;
        SourceJarTile tile = (SourceJarTile) world.getBlockEntity(loc.get());
        tile.removeSource(mana);
        return loc.get();
    }

    public static @Nullable BlockPos takeSourceNearbyWithParticles(BlockPos pos, Level world, int range, int mana){
        BlockPos result = takeSourceNearby(pos,world,range,mana);
        if(result != null){
            EntityFollowProjectile aoeProjectile = new EntityFollowProjectile(world, result, pos);
            world.addFreshEntity(aoeProjectile);
        }
        return result;
    }

    /**
     * Searches for nearby mana jars that have enough mana.
     * Returns the position where the mana was taken, or null if none were found.
     */
    public static boolean hasSourceNearby(BlockPos pos, Level world, int range, int mana){
        Optional<BlockPos> loc = BlockPos.findClosestMatch(pos, range, range, (b) -> world.getBlockEntity(b) instanceof SourceJarTile && ((SourceJarTile) world.getBlockEntity(b)).getSource() >= mana);
        return loc.isPresent();
    }

    @Nullable
    public static BlockPos canGiveSourceClosest(BlockPos pos, Level world, int range){
        Optional<BlockPos> loc = BlockPos.findClosestMatch(pos, range, range, (b) ->  world.getBlockEntity(b) instanceof SourceJarTile && ((SourceJarTile) world.getBlockEntity(b)).canAcceptSource());
        return loc.orElse(null);
    }

    public static List<BlockPos> canGiveSourceAny(BlockPos pos, Level world, int range){
        List<BlockPos> posList = new ArrayList<>();
        BlockPos.withinManhattanStream(pos, range, range, range).forEach(b ->{
            if(world.getBlockEntity(b) instanceof SourceJarTile && ((SourceJarTile) world.getBlockEntity(b)).canAcceptSource())
                posList.add(b.immutable());
        });
        return posList;
    }
}
