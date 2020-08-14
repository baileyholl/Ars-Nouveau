package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.common.block.tile.GlyphPressTile;
import com.hollingsworth.arsnouveau.common.items.ItemsRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.state.IProperty;
import net.minecraft.state.IntegerProperty;
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
    public static final IProperty stage = IntegerProperty.create("stage", 1, 31);

    public GlyphPressBlock() {
        super("glyph_press");
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
//
//            if(player.isSneaking())
//            {
//                tile.craft();
//
//                return true;
//            }

            if (tile.baseMaterial != null && player.getHeldItem(handIn).isEmpty()) {
                ItemEntity item = new ItemEntity(world, player.getPosX(), player.getPosY(), player.getPosZ(), tile.baseMaterial);
                world.addEntity(item);
                tile.baseMaterial = null;
            }
            else if (!player.inventory.getCurrentItem().isEmpty()) {
                if(player.getHeldItem(handIn).getItem() == Items.CLAY_BALL || player.getHeldItem(handIn).getItem() == ItemsRegistry.magicClay ||
                        player.getHeldItem(handIn).getItem() == ItemsRegistry.marvelousClay || player.getHeldItem(handIn).getItem() == ItemsRegistry.mythicalClay) {
                    tile.baseMaterial = player.inventory.decrStackSize(player.inventory.currentItem, 1);
                }else if(ArsNouveauAPI.getInstance().hasCraftingReagent(player.getHeldItem(handIn).getItem()) != null && tile.baseMaterial != null){
                    tile.reagentItem = player.inventory.decrStackSize(player.inventory.currentItem, 1);
                    if(!tile.craft(player)) {
                        player.inventory.addItemStackToInventory(tile.reagentItem);
                        tile.reagentItem = null;
                    }
                }
                //    System.out.println("Set stack " +  tile.itemStack);
            }
            // world.markBlocksDirtyVertical(pos.getX(), pos.getZ(), pos.getX(), pos.getZ());
            //  world.markChunkDirty(pos, tile);
            //    world.markAndNotifyBlock(pos, world.getChunkAt(pos), getDefaultState(), getDefaultState(), 2);
            world.notifyBlockUpdate(pos, state, state, 2);
        }
        return ActionResultType.PASS;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new GlyphPressTile();
    }



    @Override
    protected void fillStateContainer(StateContainer.Builder<net.minecraft.block.Block, BlockState> builder) { builder.add(stage); }

}
