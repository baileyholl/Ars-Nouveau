package com.hollingsworth.arsnouveau.common.block.tile;
import com.hollingsworth.arsnouveau.common.block.BlockRegistry;
import com.hollingsworth.arsnouveau.common.block.ManaSiphonBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;

public class ManaSiphonTile extends AbstractManaTile {
    public ManaSiphonTile() {
        super(BlockRegistry.MANA_SIPHON_TILE);
    }

    @Override
    public int getTransferRate() {
        return 100;
    }

    @Override
    public void tick() {
        if(world.isRemote )
            return;
        if(world.getGameTime() % 5 != 0)
            return;
        int radius = 3;
        int depth = 1;
        BlockPos pos = this.getPos();
        int thisY = pos.getY() -1;
//        for(int y = thisY + depth; y > thisY - depth; y--){
//            for(int x = -radius; x < radius; x++){
//                for(int z = -radius; z < radius; z++){
//                    BlockPos loc = new BlockPos(pos.getX() + x, y, pos.getZ() + z);
//                    Block block = world.getBlockState(new BlockPos(pos.getX() + x, y, pos.getZ() + z)).getBlock();
//                    if(block instanceof ManaSiphonBlock)
//                        continue;
//                    if( world.setBlockState(loc, Blocks.ICE.getDefaultState()))
//                        return;
//
//                }
//            }
//        }
        pos.getAllInBox(pos.north(2).east(2).down(1), pos.south(2).west(2).down()).forEach(t ->{
            world.setBlockState(t, Blocks.ICE.getDefaultState());
        });
//        for(int x = -radius; x < radius; x++){
//            for(int z = -radius; z < radius; z++){
//                BlockPos loc = new BlockPos(pos.getX() + x, pos.getY() - 1, pos.getZ() + z);
//                Block block = world.getBlockState(new BlockPos(pos.getX() + x, pos.getY() - 1, pos.getZ() + z)).getBlock();
//                if(block instanceof ManaSiphonBlock)
//                    continue;
//                if( world.setBlockState(loc, Blocks.ICE.getDefaultState()))
//                    return;
//
//            }
//        }

    }



}
