package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.BookwyrmLecternTile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.material.PushReaction;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

import static com.hollingsworth.arsnouveau.common.block.tile.SummoningTile.CONVERTED;

public class BookwyrmLectern extends SummonBlock{
    public static final DirectionProperty FACING = HorizontalBlock.FACING;

    public BookwyrmLectern(Properties properties, String registry) {
        super(properties, registry);
        registerDefaultState(defaultBlockState().setValue(CONVERTED, false).setValue(FACING, Direction.NORTH));
    }

    public BookwyrmLectern(String registryName) {
        super(registryName);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState p_149656_1_) {
        return PushReaction.BLOCK;
    }

    @Override
    public void neighborChanged(BlockState p_220069_1_, World world, BlockPos pos, Block p_220069_4_, BlockPos p_220069_5_, boolean p_220069_6_) {
        super.neighborChanged(p_220069_1_, world, pos, p_220069_4_, p_220069_5_, p_220069_6_);
        if(!world.isClientSide() && world.getBlockEntity(pos) instanceof BookwyrmLecternTile){
            ((BookwyrmLecternTile) world.getBlockEntity(pos)).isOff = world.hasNeighborSignal(pos);
        }
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new BookwyrmLecternTile();
    }

    public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
        return this.defaultBlockState().setValue(FACING, p_196258_1_.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockRenderType getRenderShape(BlockState p_149645_1_) {
        return BlockRenderType.MODEL;
    }

    public boolean isPathfindable(BlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
        return false;
    }

    public BlockState rotate(BlockState p_185499_1_, Rotation p_185499_2_) {
        return p_185499_1_.setValue(FACING, p_185499_2_.rotate(p_185499_1_.getValue(FACING)));
    }

    public BlockState mirror(BlockState p_185471_1_, Mirror p_185471_2_) {
        return p_185471_1_.rotate(p_185471_2_.getRotation(p_185471_1_.getValue(FACING)));
    }

}
