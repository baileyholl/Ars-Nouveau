package com.hollingsworth.arsnouveau.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class WardBlock extends ModBlock {

    public WardBlock() {
        super(defaultProperties().lightLevel((bs)->7), "warding_stone");
       // this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH));
    }
    public static final DirectionProperty FACING = HorizontalBlock.FACING;

    @Override
    public void entityInside(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
        super.entityInside(state, worldIn, pos, entityIn);
    }

    @Nullable
    @Override
    public PathNodeType getAiPathNodeType(BlockState state, IBlockReader world, BlockPos pos, @Nullable MobEntity entity) {
        return PathNodeType.LAVA;
    }


    @Deprecated
    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        if(context.getEntity() == null)
            return state.getShape(worldIn, pos);

        if(context.getEntity().level.isClientSide)
            return state.getShape(worldIn, pos);

        if(!(context.getEntity() instanceof PlayerEntity))
            return VoxelShapes.block().move(0, 1, 0);

        return VoxelShapes.block();
    }
    @Override
    public void setPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
        if (entity != null) {
            world.setBlock(pos, state.setValue(BlockStateProperties.FACING, getFacingFromEntity(pos, entity)), 2);
        }
    }
    @Override
    public boolean collisionExtendsVertically(BlockState state, IBlockReader world, BlockPos pos, Entity collidingEntity) {
        return collidingEntity instanceof MobEntity;
    }


    public static Direction getFacingFromEntity(BlockPos clickedBlock, LivingEntity entity) {
        Vector3d vec = entity.position();
        return Direction.getNearest((float) (vec.x - clickedBlock.getX()), (float) (vec.y - clickedBlock.getY()), (float) (vec.z - clickedBlock.getZ()));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.FACING);
    }
//
//    @Override
//    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
//        builder.add( FACING);
//    }
//
//    public BlockState getStateForPlacement(BlockItemUseContext context) {
//        return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing().rotateY());
//    }
//
//    public BlockState rotate(BlockState state, Rotation rot) {
//        return state.with(FACING, rot.rotate(state.get(FACING)));
//    }
}
