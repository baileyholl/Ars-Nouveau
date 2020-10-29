package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.api.item.IScribeable;
import com.hollingsworth.arsnouveau.common.block.tile.ScribesTile;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class ScribesBlock extends ModBlock{
    public ScribesBlock() {
        super(Block.Properties.create(Material.WOOD).sound(SoundType.WOOD).hardnessAndResistance(2.0f, 3.0f).notSolid(), LibBlockNames.SCRIBES_BLOCK);
    }
    public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if(handIn != Hand.MAIN_HAND)
            return ActionResultType.PASS;

        if(!world.isRemote && world.getTileEntity(pos) instanceof ScribesTile && !player.isSneaking()) {
            ScribesTile tile = (ScribesTile) world.getTileEntity(pos);
            if (tile.stack != null && player.getHeldItem(handIn).isEmpty()) {
                ItemEntity item = new ItemEntity(world, player.getPosX(), player.getPosY(), player.getPosZ(), tile.stack);
                world.addEntity(item);
                tile.stack = null;
            } else if (!player.inventory.getCurrentItem().isEmpty()) {
                if(tile.stack != null){
                    ItemEntity item = new ItemEntity(world, player.getPosX(), player.getPosY(), player.getPosZ(), tile.stack);
                    world.addEntity(item);
                }

                tile.stack = player.inventory.decrStackSize(player.inventory.currentItem, 1);

            }
            world.notifyBlockUpdate(pos, state, state, 2);
        }
        if(!world.isRemote &&  world.getTileEntity(pos) instanceof ScribesTile && player.isSneaking()){
            ItemStack stack = ((ScribesTile) world.getTileEntity(pos)).stack;
            if(stack.getItem() instanceof IScribeable){
                ((IScribeable) stack.getItem()).onScribe(world,pos,player,handIn, stack);
            }
        }

        return ActionResultType.SUCCESS;
    }

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBlockHarvested(worldIn, pos, state, player);
        if(worldIn.getTileEntity(pos) instanceof ScribesTile && ((ScribesTile) worldIn.getTileEntity(pos)).stack != null){
            worldIn.addEntity(new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), ((ScribesTile) worldIn.getTileEntity(pos)).stack));
        }
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
        if (entity != null) {
            world.setBlockState(pos, state.with(FACING, getFacingFromEntity(pos, entity)), 2);
        }
    }

    public static Direction getFacingFromEntity(BlockPos clickedBlock, LivingEntity entity) {
        Vec3d vec = entity.getPositionVec();
        Direction direction = Direction.getFacingFromVector((float) (vec.x - clickedBlock.getX()), (float) (vec.y - clickedBlock.getY()), (float) (vec.z - clickedBlock.getZ()));
        if(direction == Direction.UP || direction == Direction.DOWN)
            direction = Direction.NORTH;
        return direction;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    public BlockState rotate(BlockState state, Rotation rot) {
        return state.with(FACING, rot.rotate(state.get(FACING)));
    }
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.toRotation(state.get(FACING)));
    }
    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new ScribesTile();
    }

}
