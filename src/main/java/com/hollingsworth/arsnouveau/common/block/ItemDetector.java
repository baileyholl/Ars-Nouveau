package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.ItemDetectorTile;
import com.hollingsworth.arsnouveau.common.items.DominionWand;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class ItemDetector extends TickableModBlock{

    public ItemDetector(Properties properties) {
        super(properties);
    }

    public ItemDetector() {
        super(defaultProperties().noOcclusion());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new ItemDetectorTile(pPos, pState);
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        ItemStack stack = player.getItemInHand(handIn);
        if (handIn == InteractionHand.MAIN_HAND) {
            if(worldIn.isClientSide){
                return InteractionResult.SUCCESS;
            }
            if ((stack.getItem() instanceof DominionWand) || !(worldIn.getBlockEntity(pos) instanceof ItemDetectorTile itemDetector))
                return super.use(state, worldIn, pos, player, handIn, hit);
            if (stack.isEmpty()) {
                itemDetector.addCount(player.isShiftKeyDown() ? 8 : 1);
            }else if(!stack.isEmpty()){
                itemDetector.setFilterStack(stack.copy());
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void attack(BlockState state, Level level, BlockPos pos, Player player) {
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof ItemDetectorTile tile) {
            tile.addCount(player.isShiftKeyDown() ? -8 : -1);
        }
    }

    public int getSignal(BlockState pBlockState, BlockGetter pBlockAccess, BlockPos pPos, Direction pSide) {
        if(pBlockAccess.getBlockEntity(pPos) instanceof ItemDetectorTile detectorTile){
            return detectorTile.isPowered ? 15 : 0;
        }
        return 0;
    }
}
