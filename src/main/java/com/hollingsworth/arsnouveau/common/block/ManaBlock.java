package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.api.mana.AbstractManaTile;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

public abstract class ManaBlock extends ModBlock{
    public ManaBlock(String registryName) {
        super(registryName);
    }

    public ManaBlock(Properties properties, String registry) {
        super(properties, registry);
    }


    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if(!worldIn.isRemote && handIn == Hand.MAIN_HAND){
            if(worldIn.getTileEntity(pos) instanceof AbstractManaTile){
                AbstractManaTile tile = (AbstractManaTile) worldIn.getTileEntity(pos);
                if(player.getHeldItem(handIn).getItem() == ItemsRegistry.bucketOfMana){
                    if(tile.getMaxMana() - tile.getCurrentMana() >= 1000){
                        tile.addMana(1000);
                        if(!player.isCreative())
                            player.setHeldItem(handIn, new ItemStack(Items.BUCKET));
                    }
                    return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
                }else if(player.getHeldItem(handIn).getItem() instanceof BucketItem && ((BucketItem)player.getHeldItem(handIn).getItem()).getFluid() == Fluids.EMPTY){
                    if(tile.getCurrentMana() >= 1000 && player.addItemStackToInventory(new ItemStack(ItemsRegistry.bucketOfMana))){
                        tile.removeMana(1000);
                        player.getHeldItem(handIn).shrink(1);
                    }else if(tile.getCurrentMana() >= 1000 && player.getHeldItem(handIn).getCount() == 1){
                        tile.removeMana(1000);
                        player.setHeldItem(player.getActiveHand(),new ItemStack(ItemsRegistry.bucketOfMana));
                    }
                }
            }
        }
        return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
    }
}
