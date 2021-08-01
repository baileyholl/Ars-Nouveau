package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.ArcanePedestalTile;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class ArcanePedestal extends ModBlock{

    public ArcanePedestal() {
        super(ModBlock.defaultProperties().noOcclusion(),LibBlockNames.ARCANE_PEDESTAL);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if(handIn != Hand.MAIN_HAND)
            return ActionResultType.PASS;
        if(!world.isClientSide) {
            ArcanePedestalTile tile = (ArcanePedestalTile) world.getBlockEntity(pos);
            if (tile.stack != null && player.getItemInHand(handIn).isEmpty()) {
                if(world.getBlockState(pos.above()).getMaterial() != Material.AIR)
                    return ActionResultType.SUCCESS;
                ItemEntity item = new ItemEntity(world, player.getX(), player.getY(), player.getZ(), tile.stack);
                world.addFreshEntity(item);
                tile.stack = null;
            } else if (!player.inventory.getSelected().isEmpty()) {
                if(tile.stack != null){
                    ItemEntity item = new ItemEntity(world, player.getX(), player.getY(), player.getZ(), tile.stack);
                    world.addFreshEntity(item);
                }

                tile.stack = player.inventory.removeItem(player.inventory.selected, 1);

            }
            world.sendBlockUpdated(pos, state, state, 2);
        }
        return  ActionResultType.SUCCESS;
    }

    @Override
    public void playerWillDestroy(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
        super.playerWillDestroy(worldIn, pos, state, player);
        if(worldIn.getBlockEntity(pos) instanceof ArcanePedestalTile && ((ArcanePedestalTile) worldIn.getBlockEntity(pos)).stack != null){
            worldIn.addFreshEntity(new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), ((ArcanePedestalTile) worldIn.getBlockEntity(pos)).stack));
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return Block.box(1D, 0.0D, 1.0D, 15, 16, 15);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new ArcanePedestalTile();
    }
}
