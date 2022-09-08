package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.api.perk.ArmorPerkHolder;
import com.hollingsworth.arsnouveau.api.perk.IPerkHolder;
import com.hollingsworth.arsnouveau.api.util.PerkUtil;
import com.hollingsworth.arsnouveau.common.block.tile.AlterationTile;
import com.hollingsworth.arsnouveau.common.items.PerkItem;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class AlterationTable extends TableBlock{

    public AlterationTable() {
        super();
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (world.isClientSide || handIn != InteractionHand.MAIN_HAND || !(world.getBlockEntity(pos) instanceof AlterationTile tile))
            return InteractionResult.SUCCESS;
        ItemStack stack = player.getMainHandItem();
        // Attempt to put armor and remove perks
        if(tile.isMasterTile()){
            IPerkHolder<ItemStack> holder = PerkUtil.getPerkHolder(stack);
            if(holder instanceof ArmorPerkHolder){
                if(tile.armorStack.isEmpty()){
                    tile.setArmorStack(stack, player);
                    return InteractionResult.SUCCESS;
                }
            }else if(stack.isEmpty() && !tile.armorStack.isEmpty()){
                tile.removeArmorStack(player);
                return InteractionResult.SUCCESS;
            }
        }else{
            tile = tile.getLogicTile();
            if(tile == null)
                return InteractionResult.SUCCESS;
            if(stack.isEmpty()){
                tile.removePerk(player);
                return InteractionResult.SUCCESS;
            }
            // Attempt to change perks
            if(!(stack.getItem() instanceof PerkItem)){
                PortUtil.sendMessage(player, Component.translatable("ars_nouveau.perk.not_perk"));
                return InteractionResult.SUCCESS;
            }
            tile.addPerkStack(stack, player);
        }
        return InteractionResult.SUCCESS;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new AlterationTile(pPos, pState);
    }

    @Override
    public void playerWillDestroy(Level worldIn, BlockPos pos, BlockState state, Player player) {
        super.playerWillDestroy(worldIn, pos, state, player);
        if (worldIn.getBlockEntity(pos) instanceof AlterationTile tile) {
            tile.dropItems();
        }
    }

    // If the user breaks the other side of the table, this side needs to drop its item
    public BlockState tearDown(BlockState state, Direction direction, BlockState state2, LevelAccessor world, BlockPos pos, BlockPos pos2) {
        if (!world.isClientSide()) {
            BlockEntity entity = world.getBlockEntity(pos);
            if (entity instanceof AlterationTile tile) {
                tile.dropItems();
            }
        }
        return Blocks.AIR.defaultBlockState();
    }

}
