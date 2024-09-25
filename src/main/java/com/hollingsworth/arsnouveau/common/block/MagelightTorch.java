package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.MagelightTorchTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;

public class MagelightTorch extends TickableModBlock{
    public static final BooleanProperty FLOOR = BooleanProperty.create("floor");
    public static final BooleanProperty ROOF = BooleanProperty.create("roof");
    public MagelightTorch(){
        super(BlockBehaviour.Properties.of().sound(SoundType.STONE).strength(2.0f, 3.0f).noOcclusion().noCollission().lightLevel((b) -> b.getValue(SconceBlock.LIGHT_LEVEL)));
        registerDefaultState(defaultBlockState().setValue(BlockStateProperties.FACING, Direction.NORTH).setValue(FLOOR, true).setValue(ROOF, false));
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack stack, BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if(pLevel.isClientSide){
            return ItemInteractionResult.SUCCESS;
        }
        BlockEntity tile = pLevel.getBlockEntity(pPos);
        if(tile instanceof MagelightTorchTile torchTile){
            torchTile.setHorizontalFire(!torchTile.isHorizontalFire());
        }
        return super.useItemOn(stack, pState, pLevel, pPos, pPlayer, pHand, pHit);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction clickedDirection = context.getClickedFace();
        if(clickedDirection == Direction.UP){
            return this.defaultBlockState().setValue(BlockStateProperties.FACING, context.getNearestLookingDirection().getOpposite()).setValue(FLOOR, Boolean.TRUE);
        }else if(clickedDirection == Direction.DOWN){
            Direction direction = context.getHorizontalDirection();
            if(direction == Direction.SOUTH){
                direction = Direction.NORTH;
            }
            if(direction == Direction.WEST){
                direction = Direction.EAST;
            }
            if(direction == Direction.DOWN){
                direction = Direction.EAST;
            }
            return this.defaultBlockState().setValue(BlockStateProperties.FACING, direction).setValue(ROOF, Boolean.TRUE).setValue(FLOOR, false);
        }
        return this.defaultBlockState().setValue(BlockStateProperties.FACING, clickedDirection).setValue(FLOOR, false).setValue(ROOF, false);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(BlockStateProperties.FACING).add(SconceBlock.LIGHT_LEVEL).add(FLOOR).add(ROOF);
    }

    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(BlockStateProperties.FACING, rot.rotate(state.getValue(BlockStateProperties.FACING)));
    }

    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(BlockStateProperties.FACING)));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new MagelightTorchTile(pPos, pState);
    }
}
