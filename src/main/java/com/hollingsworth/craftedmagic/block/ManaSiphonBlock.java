package com.hollingsworth.craftedmagic.block;
import com.hollingsworth.craftedmagic.block.tile.ManaSiphonTile;
import net.minecraft.block.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.IPlantable;

import javax.annotation.Nullable;
import java.util.HashMap;

public class ManaSiphonBlock extends ModBlock {

    /**
     * Does an instanceof check on Key to check if the block may be destroyed for mana provided by value
     */
    public static HashMap<Class, SiphonData> siphon_map;


    public ManaSiphonBlock() {
        super("mana_siphon");
        siphon_map = new HashMap<>();
        siphon_map.put(FlowerBlock.class, new SiphonData(100, Blocks.IRON_BLOCK));
        siphon_map.put(GrassBlock.class, new SiphonData(20, Blocks.DIAMOND_BLOCK));
        siphon_map.put(CropsBlock.class, new SiphonData(50, Blocks.REDSTONE_BLOCK));
        
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new ManaSiphonTile();
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    public static class SiphonData{
        int mana;
        Block block;
        public SiphonData(int mana_value, Block replacementBlock){
            this.mana = mana_value;
            this.block = replacementBlock;
        }
    }

}
