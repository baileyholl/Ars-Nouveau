package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.ArmorTile;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class ArmorBlock extends TickableModBlock{


    public ArmorBlock() {
        this(defaultProperties().noOcclusion());
    }

    public ArmorBlock(Properties properties) {
        super(properties);
    }


    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {

        if (world.isClientSide || handIn != InteractionHand.MAIN_HAND || !(world.getBlockEntity(pos) instanceof ArmorTile tile))
            return InteractionResult.SUCCESS;
        if (tile.isCrafting)
            return InteractionResult.SUCCESS;


        if (!(world.getBlockState(pos.below()).getBlock() instanceof ArcaneCore)) {
            PortUtil.sendMessage(player, Component.translatable("alert.core"));
            return InteractionResult.SUCCESS;
        }
        if (tile.getStack().isEmpty()) {
            tile.addPerks(player, player.getItemInHand(handIn));
        } else {
            ItemEntity item = new ItemEntity(world, player.getX(), player.getY(), player.getZ(), tile.getStack().copy());
            world.addFreshEntity(item);
            tile.setStack(ItemStack.EMPTY);
        }
        return InteractionResult.SUCCESS;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new ArmorTile(pPos, pState);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }
}
