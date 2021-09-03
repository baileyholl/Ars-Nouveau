package com.hollingsworth.arsnouveau.api.mana;

import com.hollingsworth.arsnouveau.common.block.tile.SourcelinkTile;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.eventbus.api.Event;

import java.util.*;

public class SourcelinkEventQueue {

    public static Map<String, Set<BlockPos>> posMap = new HashMap<>();
    public static void addPosition(World world, BlockPos pos){
        String key = world.dimension().getRegistryName().toString();
        if(!posMap.containsKey(key))
            posMap.put(key, new HashSet<>());

        posMap.get(key).add(pos);
    }

    public static void addManaEvent(World world, Class<? extends SourcelinkTile> tileType, int amount, Event event, BlockPos sourcePos){
        List<BlockPos> stalePos = new ArrayList<>();
        Set<BlockPos> worldList = posMap.getOrDefault(world.dimension().getRegistryName().toString(), new HashSet<>());
        for(BlockPos p : worldList){
            TileEntity entity = world.getBlockEntity(p);
            if(world.getBlockEntity(p) == null || !(entity instanceof SourcelinkTile)){
                stalePos.add(p);
                continue;
            }
            if(entity.getClass().equals(tileType) && ((SourcelinkTile) entity).eventInRange(sourcePos, event) && ((SourcelinkTile) entity).canAcceptMana() ){
                ((SourcelinkTile) entity).getManaEvent(sourcePos, amount);
                break;
            }
        }
        for(BlockPos p : stalePos){
            worldList.remove(p);
        }
    }
}
