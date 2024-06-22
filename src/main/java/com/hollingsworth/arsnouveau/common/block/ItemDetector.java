package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.ItemDetectorTile;
import com.hollingsworth.arsnouveau.common.items.DominionWand;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class ItemDetector extends TickableModBlock{

    public static final VoxelShape shape = Shapes.join(Block.box(3, 0, 3, 13, 2, 13), Block.box(4, 2, 4, 12, 14, 12), BooleanOp.OR);

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
    public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (handIn == InteractionHand.MAIN_HAND) {
            if(worldIn.isClientSide){
                return ItemInteractionResult.SUCCESS;
            }
            if ((stack.getItem() instanceof DominionWand) || !(worldIn.getBlockEntity(pos) instanceof ItemDetectorTile itemDetector))
                return super.useItemOn(stack, state, worldIn, pos, player, handIn, hit);
            if (stack.isEmpty()) {
                itemDetector.addCount(player.isShiftKeyDown() ? 8 : 1);
            }else if(!stack.isEmpty()){
                itemDetector.setFilterStack(stack.copy());
            }
        }
        return ItemInteractionResult.SUCCESS;
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return shape;
    }

    @Override
    public void attack(BlockState state, Level level, BlockPos pos, Player player) {
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof ItemDetectorTile tile) {
            tile.addCount(player.isShiftKeyDown() ? -8 : -1);
        }
    }

    public int getSignal(BlockState pBlockState, BlockGetter pBlockAccess, BlockPos pPos, Direction pSide) {
        if(pBlockAccess.getBlockEntity(pPos) instanceof ItemDetectorTile detectorTile){
            return detectorTile.getPoweredState() ? 15 : 0;
        }
        return 0;
    }

    @Override
    public boolean isPathfindable(BlockState pState, PathComputationType pType) {
        return false;
    }
}
