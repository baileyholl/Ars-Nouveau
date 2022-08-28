package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.api.perk.IPerkHolder;
import com.hollingsworth.arsnouveau.api.util.PerkUtil;
import com.hollingsworth.arsnouveau.common.armor.MagicArmor;
import com.hollingsworth.arsnouveau.common.block.tile.AlterationTile;
import com.hollingsworth.arsnouveau.common.items.PerkItem;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
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
            if(holder instanceof MagicArmor.ArmorPerkHolder){
                if(tile.armorStack.isEmpty()){
                    tile.setArmorStack(stack, player);
                    return InteractionResult.SUCCESS;
                }
            }else if(stack.isEmpty() && !tile.armorStack.isEmpty()){
                tile.removeArmorStack(player);
                return InteractionResult.SUCCESS;
            }
        }else{
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

}
