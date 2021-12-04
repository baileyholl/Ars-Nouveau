package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.ArchwoodChestTile;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ArchwoodChest extends ChestBlock {
    public ArchwoodChest() {
        super(BlockBehaviour.Properties.of(Material.WOOD).strength(2.5F).sound(SoundType.WOOD), () -> BlockRegistry.ARCHWOOD_CHEST_TILE);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ArchwoodChestTile(pos, state);
    }


    @OnlyIn(Dist.CLIENT)
    public BlockItem provideItemBlock(Block block, Item.Properties props) {
    //    ArchwoodChestTile.setISTER(props, block);
        return new BlockItem(block, props);
    }

}
