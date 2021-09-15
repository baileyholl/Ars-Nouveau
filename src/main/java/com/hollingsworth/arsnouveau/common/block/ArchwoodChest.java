package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.ArchwoodChestTile;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

public class ArchwoodChest extends ChestBlock {
    public ArchwoodChest() {
        super(AbstractBlock.Properties.of(Material.WOOD).strength(2.5F).sound(SoundType.WOOD), () -> BlockRegistry.ARCHWOOD_CHEST_TILE);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new ArchwoodChestTile(BlockRegistry.ARCHWOOD_CHEST_TILE);
    }


    @OnlyIn(Dist.CLIENT)
    public BlockItem provideItemBlock(Block block, Item.Properties props) {
        ArchwoodChestTile.setISTER(props, block);
        return new BlockItem(block, props);
    }

}
