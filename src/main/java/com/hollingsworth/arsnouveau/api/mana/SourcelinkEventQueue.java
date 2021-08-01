package com.hollingsworth.arsnouveau.api.mana;

import com.hollingsworth.arsnouveau.common.block.tile.SourcelinkTile;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraftforge.eventbus.api.Event;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SourcelinkEventQueue {

    public static Set<BlockPos> posList = new HashSet<>();

    public static void addPosition(BlockPos pos){
        posList.add(pos);
    }

    public static void addManaEvent(IWorld world, Class<? extends SourcelinkTile> tileType, int amount, Event event, BlockPos sourcePos){
        List<BlockPos> stalePos = new ArrayList<>();
        for(BlockPos p : posList){
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
            posList.remove(p);
        }
    }
}
