package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.api.item.IScribeable;
import com.hollingsworth.arsnouveau.common.block.tile.ScribesTile;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;

import com.hollingsworth.arsnouveau.setup.BlockRegistry;

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
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;

import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nullable;

public class ScribesBlock extends ModBlock{
    public ScribesBlock() {
        super(Block.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(2.0f, 3.0f).noOcclusion(), LibBlockNames.SCRIBES_BLOCK);
        MinecraftForge.EVENT_BUS.register(this);
    }
    public static final DirectionProperty FACING = HorizontalBlock.FACING;


    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (world.isClientSide) {
            return ActionResultType.SUCCESS;
        }
        if(handIn != Hand.MAIN_HAND)
            return ActionResultType.PASS;

        if(world.getBlockEntity(pos) instanceof ScribesTile && !player.isShiftKeyDown()) {
            ScribesTile tile = (ScribesTile) world.getBlockEntity(pos);
            if (tile.stack != null && player.getItemInHand(handIn).isEmpty()) {
                ItemEntity item = new ItemEntity(world, player.getX(), player.getY(), player.getZ(), tile.stack);
                world.addFreshEntity(item);
                tile.stack = null;
            } else if (!player.inventory.getSelected().isEmpty()) {
                if(tile.stack != null){
                    ItemEntity item = new ItemEntity(world, player.getX(), player.getY(), player.getZ(), tile.stack);
                    world.addFreshEntity(item);
                }

                tile.stack = player.inventory.removeItem(player.inventory.selected, 1);

            }
            world.sendBlockUpdated(pos, state, state, 2);
        }
        if(world.getBlockEntity(pos) instanceof ScribesTile && player.isShiftKeyDown()){
            ItemStack stack = ((ScribesTile) world.getBlockEntity(pos)).stack;

            if(stack == null || stack.isEmpty())
                return ActionResultType.SUCCESS;

            if(stack.getItem() instanceof IScribeable){
                ((IScribeable) stack.getItem()).onScribe(world,pos,player,handIn, stack);

            }
        }

        return ActionResultType.SUCCESS;
    }

    @Override
    public void playerWillDestroy(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
        super.playerWillDestroy(worldIn, pos, state, player);
        if(worldIn.getBlockEntity(pos) instanceof ScribesTile && ((ScribesTile) worldIn.getBlockEntity(pos)).stack != null){
            worldIn.addFreshEntity(new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), ((ScribesTile) worldIn.getBlockEntity(pos)).stack));
        }
    }

    @Override
    public void setPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
        if (entity != null) {
            world.setBlock(pos, state.setValue(FACING, getFacingFromEntity(pos, entity)), 2);
        }
    }

    public static Direction getFacingFromEntity(BlockPos clickedBlock, LivingEntity entity) {
        Vector3d vec = entity.position();
        Direction direction = Direction.getNearest((float) (vec.x - clickedBlock.getX()), (float) (vec.y - clickedBlock.getY()), (float) (vec.z - clickedBlock.getZ()));
        if(direction == Direction.UP || direction == Direction.DOWN)
            direction = Direction.NORTH;
        return direction;
    }

    @SubscribeEvent
    public void rightClick(PlayerInteractEvent.RightClickBlock event) {
        if(!(event.getWorld().getBlockEntity(event.getPos()) instanceof ScribesTile))
            return;
        World world = event.getWorld();
        BlockPos pos = event.getPos();
        if(world.getBlockState(pos).getBlock() instanceof ScribesBlock){
            BlockRegistry.SCRIBES_BLOCK.use(world.getBlockState(pos), world, pos, event.getPlayer(), event.getHand(), null);
            event.setCanceled(true);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
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
        return new ScribesTile();
    }

}
