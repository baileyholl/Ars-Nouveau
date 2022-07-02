package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.api.source.AbstractSourceMachine;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;

public abstract class SourceBlock extends TickableModBlock {

    public SourceBlock(Properties properties, String registry) {
        super(properties);
    }


    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (!worldIn.isClientSide && handIn == InteractionHand.MAIN_HAND) {
            if (worldIn.getBlockEntity(pos) instanceof AbstractSourceMachine tile) {
                if (player.getItemInHand(handIn).getItem() == ItemsRegistry.BUCKET_OF_SOURCE.get()) {
                    if (tile.getMaxSource() - tile.getSource() >= 1000) {
                        tile.addSource(1000);
                        if (!player.isCreative())
                            player.setItemInHand(handIn, new ItemStack(Items.BUCKET));
                        return InteractionResult.SUCCESS;
                    }
                    return super.use(state, worldIn, pos, player, handIn, hit);
                } else if (player.getItemInHand(handIn).getItem() instanceof BucketItem && ((BucketItem) player.getItemInHand(handIn).getItem()).getFluid() == Fluids.EMPTY) {
                    if (tile.getSource() >= 1000) {
                        if (player.getItemInHand(handIn).getCount() == 1) {
                            player.setItemInHand(handIn, new ItemStack(ItemsRegistry.BUCKET_OF_SOURCE.get()));
                            tile.removeSource(1000);
                            return InteractionResult.SUCCESS;
                        } else if (player.addItem(new ItemStack(ItemsRegistry.BUCKET_OF_SOURCE.get()))) {
                            player.getItemInHand(handIn).shrink(1);
                            tile.removeSource(1000);
                            return InteractionResult.SUCCESS;
                        }

                    } else if (tile.getSource() >= 1000 && player.getItemInHand(handIn).getCount() == 1) {
                        tile.removeSource(1000);
                        player.setItemInHand(player.getUsedItemHand(), new ItemStack(ItemsRegistry.BUCKET_OF_SOURCE.get()));
                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }
        return super.use(state, worldIn, pos, player, handIn, hit);
    }
}
