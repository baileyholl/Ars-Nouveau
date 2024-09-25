package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.api.util.PerkUtil;
import com.hollingsworth.arsnouveau.common.block.tile.AlterationTile;
import com.hollingsworth.arsnouveau.common.items.PerkItem;
import com.hollingsworth.arsnouveau.common.items.data.StackPerkHolder;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AlterationTable extends TableBlock{

    public AlterationTable() {
        super();
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack pStack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (world.isClientSide || handIn != InteractionHand.MAIN_HAND || !(world.getBlockEntity(pos) instanceof AlterationTile tile))
            return ItemInteractionResult.SUCCESS;
        ItemStack stack = player.getMainHandItem();
        // Attempt to put armor and remove perks
        if(tile.isMasterTile()){
            var holder = PerkUtil.getPerkHolder(stack);
            if (holder instanceof StackPerkHolder) {
                if(tile.armorStack.isEmpty()){
                    tile.setArmorStack(stack, player);
                    return ItemInteractionResult.SUCCESS;
                }
            }else if(stack.isEmpty() && !tile.armorStack.isEmpty()){
                tile.removeArmorStack(player);
                return ItemInteractionResult.SUCCESS;
            }
        }else if(state.getValue(PART) == ThreePartBlock.OTHER){
            this.useItemOn(pStack, world.getBlockState(pos.below()), world, pos.below(), player, handIn, hit);
        }else{
            tile = tile.getLogicTile();
            if(tile == null)
                return ItemInteractionResult.SUCCESS;
            if(stack.isEmpty()){
                tile.removePerk(player);
                return ItemInteractionResult.SUCCESS;
            }
            // Attempt to change perks
            if(!(stack.getItem() instanceof PerkItem)){
                PortUtil.sendMessage(player, Component.translatable("ars_nouveau.perk.not_perk"));
                return ItemInteractionResult.SUCCESS;
            }
            tile.addPerkStack(stack, player);
        }

        return ItemInteractionResult.SUCCESS;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new AlterationTile(pPos, pState);
    }

    @Override
    public BlockState playerWillDestroy(Level worldIn, BlockPos pos, BlockState state, Player player) {
        super.playerWillDestroy(worldIn, pos, state, player);
        if (worldIn.getBlockEntity(pos) instanceof AlterationTile tile) {
            tile.dropItems();
        }
        return state;
    }

    public static VoxelShape SOUTH_OTHER = Shapes.or(Block.box(3.40D, 0D, 1.0D, 7.333333D, 1D, 17.0D),
            Block.box(7.333333D, 0.0D, 1.0D, 12.666667D, 4.5D, 17.0D),
            Block.box(12.666667D, 4.0D, 1.0D, 14.0D, 10.0D, 17.0D));

    public static VoxelShape NORTH_OTHER = Shapes.or(Block.box(8.666667D, 0D, -1.0D, 12.6D, 1D, 15),
            Block.box(3.333333D, 0.0D, -1.0D, 8.666667D, 4.5D, 15),
            Block.box(2.0D, 4.0D, -1.0D, 3.333333D, 10.0D, 15));

    public static VoxelShape EAST_OTHER = Shapes.or(Block.box(1.0D, 0D, 8.666667D, 17.0D, 1D, 12.6D),
            Block.box(1.0D, 0.0D, 3.333333D, 17.0D, 4.5D, 8.666667D),
            Block.box(1.0D, 4.0D, 2.0D, 17.0D, 10.0D, 3.333333D));

    public static VoxelShape WEST_OTHER = Shapes.or(Block.box(-1.0D, 0D, 3.40D, 15, 1D, 7.333333D),
            Block.box(-1.0D, 0.0D, 7.333333D, 15, 4.5D, 12.666667D),
            Block.box(-1.0D, 4.0D, 12.666667D, 15, 10.0D, 14.0D));

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        ThreePartBlock partBlock = state.getValue(PART);
        if(partBlock != ThreePartBlock.OTHER){
            return super.getShape(state, getter, pos, context);
        }
        Direction direction = state.getValue(FACING);
        if(direction == Direction.SOUTH){
            return SOUTH_OTHER;
        }else if(direction == Direction.NORTH){
            return NORTH_OTHER;
        }else if(direction == Direction.EAST){
            return EAST_OTHER;
        }else if(direction == Direction.WEST){
            return WEST_OTHER;
        }
        return super.getShape(state, getter, pos, context);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        ThreePartBlock partBlock = pState.getValue(PART);
        if(partBlock != ThreePartBlock.OTHER){
            return super.getCollisionShape(pState, pLevel, pPos, pContext);
        }
        return Shapes.empty();
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

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @javax.annotation.Nullable LivingEntity entity, ItemStack stack) {
        if (world.isClientSide) {
            return;
        }
        BlockPos blockpos = pos.relative(state.getValue(FACING));
        world.setBlock(blockpos, state.setValue(PART, ThreePartBlock.HEAD), 3);


        BlockPos lecternPos = pos.relative(Direction.UP);
        world.setBlock(lecternPos, state.setValue(PART, ThreePartBlock.OTHER), 3);


        world.blockUpdated(pos, Blocks.AIR);
        state.updateNeighbourShapes(world, pos, 3);
    }


    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState state2, LevelAccessor world, BlockPos pos, BlockPos pos2) {
        List<Direction> connectedDirs = getConnectedDirections(state);
        if(connectedDirs.contains(direction)){
           for(Direction dir : connectedDirs){
               if(world.getBlockState(pos.relative(dir)).getBlock() != this){
                   return tearDown(state, dir, state2, world, pos, pos2);
               }
           }
        }
        return super.updateShape(state, direction, state2, world, pos, pos2);
    }

    public List<Direction> getConnectedDirections(BlockState state){
        Direction direction = state.getValue(FACING);
        return switch (state.getValue(PART)) {
            case HEAD -> List.of(direction.getOpposite());
            case FOOT -> List.of(direction, Direction.UP);
            case OTHER -> List.of(Direction.DOWN);
            default -> List.of();
        };
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction direction = context.getHorizontalDirection();
        BlockPos blockpos = context.getClickedPos();
        BlockPos blockpos1 = blockpos.relative(direction);
        BlockState horizontalState = context.getLevel().getBlockState(blockpos1);
        BlockState aboveState = context.getLevel().getBlockState(blockpos.above());
        return horizontalState.canBeReplaced(context) && aboveState.canBeReplaced(context) ? this.defaultBlockState().setValue(FACING, direction) : null;
    }

    @Override
    protected boolean isPathfindable(BlockState pState, PathComputationType pPathComputationType) {
        return false;
    }
}
