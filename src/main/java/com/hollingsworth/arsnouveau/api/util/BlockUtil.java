package com.hollingsworth.arsnouveau.api.util;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.event.ForgeEventFactory;

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

        if(ForgeEventFactory.doPlayerHarvestCheck(caster instanceof PlayerEntity ?
                (PlayerEntity) caster : FakePlayerFactory.getMinecraft((ServerWorld) world), world.getBlockState(pos), true)){
            return world.destroyBlock(pos, dropBlock);
        }
        return false;
    }

    public static boolean destroyRespectsClaim(LivingEntity caster, World world, BlockPos pos){
        return ForgeEventFactory.doPlayerHarvestCheck(caster instanceof PlayerEntity ?
                (PlayerEntity) caster : FakePlayerFactory.getMinecraft((ServerWorld) world), world.getBlockState(pos), true);
    }

}
