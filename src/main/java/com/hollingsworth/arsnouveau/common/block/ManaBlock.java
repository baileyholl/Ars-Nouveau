package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.api.mana.AbstractManaTile;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.Level;

public abstract class ManaBlock extends TickableModBlock {
    public ManaBlock(String registryName) {
        super(registryName);
    }

    public ManaBlock(Properties properties, String registry) {
        super(properties, registry);
    }


    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if(!worldIn.isClientSide && handIn == InteractionHand.MAIN_HAND){
            if(worldIn.getBlockEntity(pos) instanceof AbstractManaTile){
                AbstractManaTile tile = (AbstractManaTile) worldIn.getBlockEntity(pos);
                if(player.getItemInHand(handIn).getItem() == ItemsRegistry.BUCKET_OF_SOURCE){
                    if(tile.getMaxMana() - tile.getCurrentMana() >= 1000){
                        tile.addMana(1000);
                        if(!player.isCreative())
                            player.setItemInHand(handIn, new ItemStack(Items.BUCKET));
                    }
                    return super.use(state, worldIn, pos, player, handIn, hit);
                }else if(player.getItemInHand(handIn).getItem() instanceof BucketItem && ((BucketItem)player.getItemInHand(handIn).getItem()).getFluid() == Fluids.EMPTY){
                    if(tile.getCurrentMana() >= 1000){
                        if(player.getItemInHand(handIn).getCount() == 1){
                            player.setItemInHand(handIn, new ItemStack(ItemsRegistry.BUCKET_OF_SOURCE));
                            tile.removeMana(1000);
                        }else if(player.addItem(new ItemStack(ItemsRegistry.BUCKET_OF_SOURCE))) {
                            player.getItemInHand(handIn).shrink(1);
                            tile.removeMana(1000);
                        }
                    }else if(tile.getCurrentMana() >= 1000 && player.getItemInHand(handIn).getCount() == 1){
                        tile.removeMana(1000);
                        player.setItemInHand(player.getUsedItemHand(),new ItemStack(ItemsRegistry.BUCKET_OF_SOURCE));
                    }
                }
            }
        }
        return super.use(state, worldIn, pos, player, handIn, hit);
    }
}
