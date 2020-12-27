package com.hollingsworth.arsnouveau.api.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

import net.minecraft.fluid.FluidState;
import net.minecraft.tags.BlockTags;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class BlockUtil {

    public static boolean isTreeBlock(Block block){
        return block.isIn(BlockTags.LEAVES) || block.isIn(BlockTags.LOGS);
    }

    public static boolean containsStateInRadius(World world, BlockPos start, int radius, Class clazz){
        for(double x = start.getX() - radius; x <= start.getX() + radius; x++){
            for(double y = start.getY() - radius; y <= start.getY() + radius; y++){
                for(double z = start.getZ() - radius; z <= start.getZ() + radius; z++){
                    BlockPos pos = new BlockPos( x, y, z);
                    if(!pos.equals(start) && world.getBlockState(pos).getBlock().getClass().equals(clazz)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static double distanceFrom(BlockPos start, BlockPos end){
        return Math.sqrt(Math.pow(start.getX() - end.getX(), 2) + Math.pow(start.getY() - end.getY(), 2) + Math.pow(start.getZ() - end.getZ(), 2));
    }

    public static boolean destroyBlockSafely(World world, BlockPos pos, boolean dropBlock, LivingEntity caster){
        if(!(world instanceof ServerWorld))
            return false;
        PlayerEntity playerEntity = caster instanceof PlayerEntity ? (PlayerEntity) caster : FakePlayerFactory.getMinecraft((ServerWorld) world);
        if(MinecraftForge.EVENT_BUS.post(new BlockEvent.BreakEvent(world, pos, world.getBlockState(pos),playerEntity)))
            return false;
        world.getBlockState(pos).getBlock().onBlockHarvested(world, pos, world.getBlockState(pos), playerEntity);
        return world.destroyBlock(pos, dropBlock);

    }

    public static boolean destroyRespectsClaim(LivingEntity caster, World world, BlockPos pos){
        PlayerEntity playerEntity = caster instanceof PlayerEntity ? (PlayerEntity) caster : FakePlayerFactory.getMinecraft((ServerWorld) world);
        return !MinecraftForge.EVENT_BUS.post(new BlockEvent.BreakEvent(world, pos, world.getBlockState(pos),playerEntity));
    }

    public static void safelyUpdateState(World world, BlockPos pos, BlockState state){
        if(!World.isOutsideBuildHeight(pos))
            world.notifyBlockUpdate(pos, state, state, 3);
    }

    public static void safelyUpdateState(World world, BlockPos pos){
        safelyUpdateState(world, pos, world.getBlockState(pos));
    }

    public static boolean destroyBlockSafelyWithoutSound(World world, BlockPos pos, boolean dropBlock){
        return destroyBlockWithoutSound(world, pos, dropBlock, null);
    }

    public static boolean destroyBlockSafelyWithoutSound(World world, BlockPos pos, boolean dropBlock, @Nullable LivingEntity caster){
        if(!(world instanceof ServerWorld))
            return false;

        PlayerEntity playerEntity = caster instanceof PlayerEntity ? (PlayerEntity) caster : FakePlayerFactory.getMinecraft((ServerWorld) world);
        if(MinecraftForge.EVENT_BUS.post(new BlockEvent.BreakEvent(world, pos, world.getBlockState(pos),playerEntity)))
            return false;

        return destroyBlockWithoutSound(world, pos, dropBlock);
    }

    private static boolean destroyBlockWithoutSound(World world, BlockPos pos, boolean dropBlock) {
        return destroyBlockWithoutSound(world, pos, dropBlock, (Entity)null);
    }

    private static boolean destroyBlockWithoutSound(World world, BlockPos pos, boolean isMoving, @Nullable Entity entityIn){
        BlockState blockstate = world.getBlockState(pos);
        if (blockstate.isAir(world, pos)) {
            return false;
        } else {
            FluidState ifluidstate = world.getFluidState(pos);
            if (isMoving) {
                TileEntity tileentity = blockstate.hasTileEntity() ? world.getTileEntity(pos) : null;
                Block.spawnDrops(blockstate, world, pos, tileentity, entityIn, ItemStack.EMPTY);
            }

            return world.setBlockState(pos, ifluidstate.getBlockState(), 3);
        }
    }

    public static List<IItemHandler> getAdjacentInventories(World world, BlockPos pos){
        if(world == null || pos == null)return new ArrayList<>();
        ArrayList<IItemHandler> iInventories = new ArrayList<>();
        for(Direction d : Direction.values()){
            TileEntity tileEntity = world.getTileEntity(pos.offset(d));
            if(tileEntity == null)
                continue;

            if(tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).isPresent())
                iInventories.add(tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null));
        }

        return iInventories;
    }

    public static ItemStack insertItemAdjacent(World world, BlockPos pos, ItemStack stack){
        for(IItemHandler i : BlockUtil.getAdjacentInventories(world, pos)){
            if(stack == ItemStack.EMPTY || stack == null)
                break;
            stack = ItemHandlerHelper.insertItemStacked(i, stack, false);
        }
        return stack;
    }

    public static ItemStack getItemAdjacent(World world, BlockPos pos, Predicate<ItemStack> matchPredicate){
        ItemStack stack = ItemStack.EMPTY;
        for(IItemHandler inv : BlockUtil.getAdjacentInventories(world, pos)){
            for(int i = 0; i < inv.getSlots(); ++i) {
                if(matchPredicate.test(inv.getStackInSlot(i)))
                    return inv.getStackInSlot(i);
            }
        }
        return stack;
    }

    private BlockUtil(){};

}
