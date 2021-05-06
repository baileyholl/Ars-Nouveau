package com.hollingsworth.arsnouveau.common.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.hollingsworth.arsnouveau.common.block.tile.SconceTile;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;

import java.util.Map;

import static com.hollingsworth.arsnouveau.common.block.ScribesBlock.getFacingFromEntity;

public class SconceBlock extends ModBlock{
    private static final Map<Direction, VoxelShape> AABBS =
            Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, Block.box(5D, 3.0D, 11.0D, 11D, 13.0D, 16.0D),
                    Direction.SOUTH, Block.box(5.0, 3.0D, 0.0D, 11D, 13.0D, 5.0D),
                    Direction.WEST, Block.box(11.0D, 3.0D, 5.5D, 16.0D, 13.0D, 10.5D), Direction.EAST, Block.box(0.0D, 3.0D, 5.5D, 5.0D, 13.0D, 10.5D)));
    public static final Property<Integer> LIGHT_LEVEL = IntegerProperty.create("level", 0, 15);

    public SconceBlock(String registryName) {
        super(Block.Properties.of(Material.STONE).sound(SoundType.STONE).strength(2.0f, 3.0f).noOcclusion().noCollission().lightLevel((b) -> b.getValue(LIGHT_LEVEL)), registryName);
    }
    public static final DirectionProperty FACING = HorizontalBlock.FACING;

    @Override
    public void setPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
        if (entity != null) {
            world.setBlock(pos, state.setValue(FACING, getFacingFromEntity(pos, entity)).setValue(LIGHT_LEVEL, 0), 2);
        }
    }

    public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
        return getShape(p_220053_1_);
    }

    public static VoxelShape getShape(BlockState p_220289_0_) {
        return AABBS.get(p_220289_0_.getValue(FACING));
    }


    public BlockState updateShape(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
        return p_196271_2_.getOpposite() == p_196271_1_.getValue(FACING) && !p_196271_1_.canSurvive(p_196271_4_, p_196271_5_) ? Blocks.AIR.defaultBlockState() : p_196271_1_;
    }
    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
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
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new SconceTile();
    }
}
