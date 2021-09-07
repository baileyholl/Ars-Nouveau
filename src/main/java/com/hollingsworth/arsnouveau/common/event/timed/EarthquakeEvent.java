package com.hollingsworth.arsnouveau.common.event.timed;

import com.hollingsworth.arsnouveau.api.event.ITimedEvent;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class EarthquakeEvent implements ITimedEvent {

    int ticks;
    BlockPos origin;
    World world;
    List<BlockPos> posList = new ArrayList<>();
    int counter;
    public EarthquakeEvent(World world, BlockPos origin, BlockPos destination){
        this.origin = origin;
        this.world = world;
        //for(BlockPos vec : BlockUtil.getLine(origin.getX(), origin.getZ(), destination.getX(), destination.getZ(), 2f)){
            //System.out.println(vec.toString());
            //BlockPos adjustedPos = new BlockPos(vec.getX(), origin.getY(), vec.getZ());
           // posList.add(adjustedPos);
//            for(BlockPos p : BlockPos.withinManhattan(adjustedPos.north(4).east(4), 3, 3, 3)){
//                posList.add(p);
//            }
//            for(BlockPos p : BlockPos.betweenClosed(adjustedPos.north(4).east(4), adjustedPos.south(4).west(4))) {
//                if(!posList.contains(p))
//                    posList.add(p.immutable());
//            }
          //  posList.add(new BlockPos(vec.getX(), origin.getY(), vec.getZ()));
        //}
    }

    @Override
    public void tick(boolean serverSide) {

//        counter += 2;
//        if(counter >= posList.size())
//            counter = posList.size();

        for(int i = 0; i < 5; i++){
            if(counter < posList.size()){
                if(world.getRandom().nextFloat() < 0.5) {

                    BlockPos p = posList.get(counter);
                    FallingBlockEntity blockEntity = new FallingBlockEntity(world, p.getX() + 0.5, p.getY(), p.getZ() + 0.5, world.getBlockState(p));
                    blockEntity.setDeltaMovement(0, 0.5 + ParticleUtil.inRange(-0.1, 0.1), 0.0);
                    world.addFreshEntity(blockEntity);
                }
            }
            counter++;
        }
//        if(counter < posList.size()){
//            BlockPos p = posList.get(counter);
//            FallingBlockEntity blockEntity = new FallingBlockEntity(world, p.getX() + 0.5, p.getY(), p.getZ() + 0.5, world.getBlockState(p));
//            blockEntity.setDeltaMovement(0, 0.5 + ParticleUtil.inRange(-0.1, 0.2), 0.0);
//            world.addFreshEntity(blockEntity);
//        }
//        ticks++;
//        int expansionRate = 2;
//        int numExpanded = ticks / expansionRate;
//        BlockPos pos1 = origin;
//        if(ticks % 2 == 0){
//            for(BlockPos p : BlockPos.betweenClosed(origin.north(numExpanded).east(numExpanded), origin.south(numExpanded).west(numExpanded))) {
//                if(world.random.nextFloat() < 0.5 && !iteratedPos.contains(p) && !world.getBlockState(p).isAir()) {
//                    FallingBlockEntity blockEntity = new FallingBlockEntity(world, p.getX() + 0.5, p.getY(), p.getZ() + 0.5, world.getBlockState(p));
//                    blockEntity.setDeltaMovement(0, 0.5 + ParticleUtil.inRange(-0.1, 0.2), 0.0);
////                    blockEntity.setNoGravity(true);
//                    world.addFreshEntity(blockEntity);
//                    iteratedPos.add(p.immutable());
//                }
//            }
//
//        }
    }

    @Override
    public boolean isExpired() {
        return counter > posList.size();
    }
}
