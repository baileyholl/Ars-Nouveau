package com.hollingsworth.arsnouveau.api.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
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

import static java.lang.Math.abs;

public class BlockUtil {

    public static boolean isTreeBlock(Block block){
        return block.is(BlockTags.LEAVES) || block.is(BlockTags.LOGS);
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
        if(start == null || end == null)
            return 0;
        return Math.sqrt(Math.pow(start.getX() - end.getX(), 2) + Math.pow(start.getY() - end.getY(), 2) + Math.pow(start.getZ() - end.getZ(), 2));
    }

    public static double distanceFrom(Vector3d start, BlockPos end){
        if(start == null || end == null)
            return 0;
        return Math.sqrt(Math.pow(start.x - end.getX(), 2) + Math.pow(start.y - end.getY(), 2) + Math.pow(start.z - end.getZ(), 2));
    }

    public static double distanceFrom(Vector3d start, Vector3d end){
        return Math.sqrt(Math.pow(start.x - end.x, 2) + Math.pow(start.y - end.y, 2) + Math.pow(start.z - end.z, 2));
    }
    public static boolean destroyBlockSafely(World world, BlockPos pos, boolean dropBlock, LivingEntity caster){
        if(!(world instanceof ServerWorld))
            return false;
        PlayerEntity playerEntity = caster instanceof PlayerEntity ? (PlayerEntity) caster : FakePlayerFactory.getMinecraft((ServerWorld) world);
        if(MinecraftForge.EVENT_BUS.post(new BlockEvent.BreakEvent(world, pos, world.getBlockState(pos),playerEntity)))
            return false;
        world.getBlockState(pos).getBlock().playerWillDestroy(world, pos, world.getBlockState(pos), playerEntity);
        return world.destroyBlock(pos, dropBlock);

    }

    public static boolean destroyRespectsClaim(LivingEntity caster, World world, BlockPos pos){
        PlayerEntity playerEntity = caster instanceof PlayerEntity ? (PlayerEntity) caster : FakePlayerFactory.getMinecraft((ServerWorld) world);
        return !MinecraftForge.EVENT_BUS.post(new BlockEvent.BreakEvent(world, pos, world.getBlockState(pos),playerEntity));
    }

    public static void safelyUpdateState(World world, BlockPos pos, BlockState state){
        if(!World.isOutsideBuildHeight(pos))
            world.sendBlockUpdated(pos, state, state, 3);
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
                TileEntity tileentity = blockstate.hasTileEntity() ? world.getBlockEntity(pos) : null;
                Block.dropResources(blockstate, world, pos, tileentity, entityIn, ItemStack.EMPTY);
            }

            return world.setBlock(pos, ifluidstate.createLegacyBlock(), 3);
        }
    }

    public static List<IItemHandler> getAdjacentInventories(World world, BlockPos pos){
        if(world == null || pos == null)return new ArrayList<>();
        ArrayList<IItemHandler> iInventories = new ArrayList<>();
        for(Direction d : Direction.values()){
            TileEntity tileEntity = world.getBlockEntity(pos.relative(d));
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


    public static List<BlockPos> getLine(int x0, int y0, int x1, int y1, float wd)
    {
        List<BlockPos> vects = new ArrayList<>();
        int dx = abs(x1-x0), sx = x0 < x1 ? 1 : -1;
        int dy = abs(y1-y0), sy = y0 < y1 ? 1 : -1;
        int err = dx-dy, e2, x2, y2;                          /* error value e_xy */
        float ed = dx+dy == 0 ? 1 : MathHelper.sqrt((float) dx * dx + (float) dy * dy);

        for (wd = (wd+1)/2; ; ) {                                   /* pixel loop */
            vects.add(new BlockPos(x0,0, y0));
            e2 = err; x2 = x0;
            if (2*e2 >= -dx) {                                           /* x step */
                for (e2 += dy, y2 = y0; e2 < ed*wd && (y1 != y2 || dx > dy); e2 += dx) {
                    vects.add(new BlockPos(x0,0, y2 += sy));
                }
                if (x0 == x1) break;
                e2 = err; err -= dy; x0 += sx;
            }
            if (2*e2 <= dy) {                                            /* y step */
                for (e2 = dx-e2; e2 < ed*wd && (x1 != x2 || dx < dy); e2 += dy) {
                    vects.add(new BlockPos(x2 += sx, 0, y0));
                }
                if (y0 == y1) break;
                err += dx; y0 += sy;
            }
        }
        return vects;
    }

    private BlockUtil(){};

}
