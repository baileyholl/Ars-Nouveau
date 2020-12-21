package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.EnchantingApparatusTile;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class EnchantingApparatusBlock extends ModBlock{

    public EnchantingApparatusBlock() {
        super(ModBlock.defaultProperties().notSolid(),"enchanting_apparatus");
    }


    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public void onBlockClicked(BlockState state, World worldIn, BlockPos pos, PlayerEntity player) {
        super.onBlockClicked(state, worldIn, pos, player);

    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if(world.isRemote || handIn != Hand.MAIN_HAND)
            return ActionResultType.SUCCESS;
        EnchantingApparatusTile tile = (EnchantingApparatusTile) world.getTileEntity(pos);
        if(tile.isCrafting)
            return ActionResultType.SUCCESS;

        if(!player.getHeldItemOffhand().isEmpty() && tile.catalystItem != null && !tile.catalystItem.isEmpty()){
            tile.attemptCraft();
            return ActionResultType.SUCCESS;
        }


        if(!(world.getBlockState(pos.down()).getBlock() instanceof ArcaneCore)){
            PortUtil.sendMessage(player, new TranslationTextComponent("alert.core"));
            return ActionResultType.SUCCESS;
        }

        if(player.isSneaking()){
            tile.attemptCraft();
            return ActionResultType.SUCCESS;
        }
        if (tile.catalystItem != null && !tile.catalystItem.isEmpty() && player.getHeldItem(handIn).isEmpty()) {
            ItemEntity item = new ItemEntity(world, player.getPosX(), player.getPosY(), player.getPosZ(), tile.catalystItem);
            world.addEntity(item);
            tile.catalystItem = null;
        } else if (!player.inventory.getCurrentItem().isEmpty()) {
            if(tile.catalystItem != null){
                ItemEntity item = new ItemEntity(world, player.getPosX(), player.getPosY(), player.getPosZ(), tile.catalystItem);
                world.addEntity(item);
            }
            tile.catalystItem = player.inventory.decrStackSize(player.inventory.currentItem, 1);;
        }
        world.notifyBlockUpdate(pos, state, state, 2);

        return ActionResultType.SUCCESS;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return Block.makeCuboidShape(1D, 1.0D, 1.0D, 15, 16, 15);
    }

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBlockHarvested(worldIn, pos, state, player);
        if(worldIn.getTileEntity(pos) instanceof EnchantingApparatusTile && ((EnchantingApparatusTile) worldIn.getTileEntity(pos)).catalystItem != null){
            worldIn.addEntity(new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), ((EnchantingApparatusTile) worldIn.getTileEntity(pos)).catalystItem));
        }
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new EnchantingApparatusTile();
    }

    @Override
    public BlockRenderType getRenderType(BlockState p_149645_1_) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }
}
