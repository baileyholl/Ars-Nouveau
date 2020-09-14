package com.hollingsworth.arsnouveau.api.util;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.world.BlockEvent;

public class BlockUtil {

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
}
