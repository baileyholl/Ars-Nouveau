package com.hollingsworth.arsnouveau.common.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.hollingsworth.arsnouveau.common.block.tile.SconceTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Map;

public class SconceBlock extends TickableModBlock {
    private static final Map<Direction, VoxelShape> AABBS =
            Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, Block.box(5D, 3.0D, 11.0D, 11D, 13.0D, 16.0D),
                    Direction.SOUTH, Block.box(5.0, 3.0D, 0.0D, 11D, 13.0D, 5.0D),
                    Direction.WEST, Block.box(11.0D, 3.0D, 5.5D, 16.0D, 13.0D, 10.5D), Direction.EAST, Block.box(0.0D, 3.0D, 5.5D, 5.0D, 13.0D, 10.5D)));
    public static final Property<Integer> LIGHT_LEVEL = IntegerProperty.create("level", 0, 15);

    public SconceBlock() {
        super(Block.Properties.of(Material.STONE).sound(SoundType.STONE).strength(2.0f, 3.0f).noOcclusion().noCollission().lightLevel((b) -> b.getValue(LIGHT_LEVEL)));
    }

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        if (context.getClickedFace().getAxis().isHorizontal()) {
            return this.defaultBlockState().setValue(FACING, context.getClickedFace());
        } else {
            // The player tried to place on the floor or ceiling. Sconces don't have models for those facings.
            return null; // Block the placement outright
        }
    }

    public VoxelShape getShape(BlockState p_220053_1_, BlockGetter p_220053_2_, BlockPos p_220053_3_, CollisionContext p_220053_4_) {
        return getShape(p_220053_1_);
    }

    public static VoxelShape getShape(BlockState p_220289_0_) {
        return AABBS.get(p_220289_0_.getValue(FACING));
    }


    public BlockState updateShape(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, LevelAccessor p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
        return p_196271_2_.getOpposite() == p_196271_1_.getValue(FACING) && !p_196271_1_.canSurvive(p_196271_4_, p_196271_5_) ? Blocks.AIR.defaultBlockState() : p_196271_1_;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
        builder.add(LIGHT_LEVEL);
    }

    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SconceTile(pos, state);
    }
}
