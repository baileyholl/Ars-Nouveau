package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.PotionJarTile;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class PotionJar extends ModBlock{
    public PotionJar(Properties properties, String registry) {
        super(properties, registry);
    }

    public PotionJar(String registry){
        super(registry);
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
//        if(worldIn.isRemote)
//            return ActionResultType.SUCCESS;

        PotionJarTile tile = (PotionJarTile) worldIn.getTileEntity(pos);
        ItemStack stack = player.getHeldItem(handIn);
        Potion potion = PotionUtils.getPotionFromItem(stack);
        if(stack.getItem() == Items.POTION && potion != Potions.EMPTY ) {
            if (tile.amount == 0) {

                tile.setPotion(PotionUtils.getPotionFromItem(player.getHeldItem(handIn)));
                if(!worldIn.isRemote) {
                    tile.amount += 100;
                    if(!player.isCreative()) {
                        player.addItemStackToInventory(new ItemStack(Items.GLASS_BOTTLE));
                        stack.shrink(1);
                    }
                }
            }else if(tile.getPotion() == potion && tile.getCurrentFill() < tile.getMaxFill()){
                if(!worldIn.isRemote) {
                    tile.amount += 100;
                    if(!player.isCreative()) {
                        player.addItemStackToInventory(new ItemStack(Items.GLASS_BOTTLE));
                        stack.shrink(1);
                    }
                }
            }
            worldIn.notifyBlockUpdate(pos, worldIn.getBlockState(pos), worldIn.getBlockState(pos), 3);
        }

        if(!worldIn.isRemote && stack.getItem() == Items.GLASS_BOTTLE && tile.getCurrentFill() >= 100){
            tile.amount -= 100;
            ItemStack potionStack = new ItemStack(Items.POTION);
            PotionUtils.addPotionToItemStack(potionStack, tile.getPotion());
            player.addItemStackToInventory(potionStack);
            player.getHeldItem(handIn).shrink(1);
        }
        if(worldIn.isRemote)
            return ActionResultType.SUCCESS;

        return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public BlockRenderType getRenderType(BlockState p_149645_1_) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<net.minecraft.block.Block, BlockState> builder) { builder.add(ManaJar.fill); }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new PotionJarTile();
    }
}
