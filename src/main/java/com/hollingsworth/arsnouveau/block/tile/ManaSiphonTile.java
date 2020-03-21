package com.hollingsworth.arsnouveau.block.tile;
import com.hollingsworth.arsnouveau.block.BlockRegistry;
import com.hollingsworth.arsnouveau.block.ManaSiphonBlock;
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
        if(world.getGameTime() % 20 != 0)
            return;
        int radius = 10;
        int depth = 3;
        BlockPos pos = this.getPos();
        int thisY = pos.getY();
        for(int y = thisY + depth; y > thisY - depth; y--){
            for(int x = -radius; x < radius; x++){
                for(int z = -radius; z < radius; z++){
                    BlockPos loc = new BlockPos(pos.getX() + x, y, pos.getZ() + z);
                    Block block = world.getBlockState(new BlockPos(pos.getX() + x, y, pos.getZ() + z)).getBlock();
                    if(block instanceof ManaSiphonBlock)
                        continue;
                    if( world.setBlockState(loc, Blocks.ICE.getDefaultState()))
                        return;

                }
            }
        }

    }



}
