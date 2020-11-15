package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.GlyphPressTile;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class GlyphPressBlock extends ModBlock{
    public static final Property<Integer> stage = IntegerProperty.create("stage", 1, 31);

    public GlyphPressBlock() {
        super(ModBlock.defaultProperties().notSolid(),"glyph_press");
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult p_225533_6_) {
        if(!world.isRemote) {
            GlyphPressTile tile = (GlyphPressTile) world.getTileEntity(pos);
            if(tile.isCrafting)
                return ActionResultType.PASS;

            if (tile.baseMaterial != null && !tile.baseMaterial.isEmpty() && player.getHeldItem(handIn).isEmpty()) {
                ItemEntity item = new ItemEntity(world, player.getPosX(), player.getPosY(), player.getPosZ(), tile.baseMaterial);
                world.addEntity(item);
                tile.baseMaterial = ItemStack.EMPTY;
            }
            else if (!player.inventory.getCurrentItem().isEmpty()) {
                if(player.getHeldItem(handIn).getItem() == Items.CLAY_BALL || player.getHeldItem(handIn).getItem() == ItemsRegistry.magicClay ||
                        player.getHeldItem(handIn).getItem() == ItemsRegistry.marvelousClay || player.getHeldItem(handIn).getItem() == ItemsRegistry.mythicalClay) {
                    if(tile.baseMaterial != null && !tile.baseMaterial.isEmpty()){
                        ItemEntity item = new ItemEntity(world, player.getPosX(), player.getPosY(), player.getPosZ(), tile.baseMaterial);
                        world.addEntity(item);
                    }
                    tile.baseMaterial = player.inventory.decrStackSize(player.inventory.currentItem, 1);
                }else if(tile.baseMaterial != null && !tile.baseMaterial.isEmpty()){
                    if(tile.reagentItem != null && !tile.reagentItem.isEmpty()){
                        ItemEntity item = new ItemEntity(world, player.getPosX(), player.getPosY(), player.getPosZ(), tile.reagentItem);
                        world.addEntity(item);
                    }

                    tile.reagentItem = player.inventory.decrStackSize(player.inventory.currentItem, 1);
                    if(!tile.craft(player) && player.inventory.addItemStackToInventory(tile.reagentItem)) {
                        tile.reagentItem = ItemStack.EMPTY;
                    }
                }
            }

            world.notifyBlockUpdate(pos, state, state, 2);
        }
        return ActionResultType.SUCCESS;
    }


    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBlockHarvested(worldIn, pos, state, player);
        if(!(worldIn.getTileEntity(pos) instanceof GlyphPressTile) || worldIn.isRemote)
            return;
        GlyphPressTile tile = ((GlyphPressTile) worldIn.getTileEntity(pos));
        if(tile.baseMaterial != null && !tile.baseMaterial.isEmpty()){
            worldIn.addEntity(new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), tile.baseMaterial));
            if(tile.reagentItem != null && !tile.reagentItem.isEmpty()){
                worldIn.addEntity(new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), tile.reagentItem));
            }

        }
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new GlyphPressTile();
    }



    @Override
    protected void fillStateContainer(StateContainer.Builder<net.minecraft.block.Block, BlockState> builder) { builder.add(stage); }

}
