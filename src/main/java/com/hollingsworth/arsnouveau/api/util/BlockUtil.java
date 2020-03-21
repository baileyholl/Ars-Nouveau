package com.hollingsworth.arsnouveau.api.util;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockUtil {

    public static boolean containsStateInRadius(World world, BlockPos start, int radius, Class clazz){
        for(double x = start.getX() - radius; x <= start.getX() + radius; x++){
            for(double y = start.getY() - radius; y <= start.getY() + radius; y++){
                for(double z = start.getZ() - radius; z <= start.getZ() + radius; z++){
                    BlockPos pos = new BlockPos( x, y, z);
                    if(!pos.equals(start) && world.getBlockState(pos).getBlock().getClass().equals(clazz)) {
                        System.out.println(start);
                        System.out.println( pos);
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

}
