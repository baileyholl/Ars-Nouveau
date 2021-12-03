package com.hollingsworth.arsnouveau.common.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class WardBlock extends ModBlock {

    public WardBlock() {
        super(defaultProperties().lightLevel((bs)->7), "warding_stone");
       // this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH));
    }
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    @Override
    public void entityInside(BlockState state, Level worldIn, BlockPos pos, Entity entityIn) {
        super.entityInside(state, worldIn, pos, entityIn);
    }

    @Nullable
    @Override
    public BlockPathTypes getAiPathNodeType(BlockState state, BlockGetter world, BlockPos pos, @Nullable Mob entity) {
        return BlockPathTypes.LAVA;
    }


//    @Deprecated
//    public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
//        if(context.getEntity() == null)
//            return state.getShape(worldIn, pos);
//
//        if(context.getEntity().level.isClientSide)
//            return state.getShape(worldIn, pos);
//
//        if(!(context.getEntity() instanceof Player))
//            return Shapes.block().move(0, 1, 0);
//
//        return Shapes.block();
//    }
    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
        if (entity != null) {
            world.setBlock(pos, state.setValue(BlockStateProperties.FACING, getFacingFromEntity(pos, entity)), 2);
        }
    }
    @Override
    public boolean collisionExtendsVertically(BlockState state, BlockGetter world, BlockPos pos, Entity collidingEntity) {
        return collidingEntity instanceof Mob;
    }


    public static Direction getFacingFromEntity(BlockPos clickedBlock, LivingEntity entity) {
        Vec3 vec = entity.position();
        return Direction.getNearest((float) (vec.x - clickedBlock.getX()), (float) (vec.y - clickedBlock.getY()), (float) (vec.z - clickedBlock.getZ()));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.FACING);
    }
}
