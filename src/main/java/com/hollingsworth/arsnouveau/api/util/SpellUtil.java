package com.hollingsworth.arsnouveau.api.util;

import com.hollingsworth.arsnouveau.api.event.SpellCastEvent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraftforge.common.MinecraftForge;

import java.util.ArrayList;
import java.util.List;

public class SpellUtil {

    public static boolean postEvent(SpellCastEvent e){
        return MinecraftForge.EVENT_BUS.post(e);
    }

    public static List<BlockPos> calcAOEBlocks(LivingEntity caster, BlockPos origin, BlockRayTraceResult mop, int aoeBonus) {
        return calcAOEBlocks(caster, origin, mop, 1 + aoeBonus, 1 + aoeBonus, 1, -1);
    }

    public static List<BlockPos> calcAOEBlocks(LivingEntity caster, BlockPos origin, BlockRayTraceResult mop, int aoeBonus, int pierceBonus) {
        return calcAOEBlocks(caster, origin, mop, 1 + aoeBonus, 1 + aoeBonus, 1 + pierceBonus, -1);
    }

    public static List<BlockPos> calcAOEBlocks(LivingEntity caster, BlockPos origin, BlockRayTraceResult mop, int width, int height, int depth, int distance) {
        return calcAOEBlocks(caster.getHorizontalFacing().getDirectionVec(), origin, mop, width, height, depth, distance);
    }

    public static List<BlockPos> calcAOEBlocks(Vector3d hitVec, BlockPos origin, BlockRayTraceResult mop, int width, int height, int depth, int distance) {
        return calcAOEBlocks(Direction.getFacingFromVector(hitVec.x, hitVec.y, hitVec.z).getOpposite().getDirectionVec(), origin, mop, width, height, depth, distance);
    }
    // https://github.com/SlimeKnights/TinkersConstruct/blob/1.12/src/main/java/slimeknights/tconstruct/library/utils/ToolHelper.java
    public static List<BlockPos> calcAOEBlocks(Vector3i facingVec, BlockPos origin, BlockRayTraceResult mop, int width, int height, int depth, int distance) {
        // we know the block and we know which side of the block we're hitting. time to calculate the depth along the different axes
        int x, y, z;
        BlockPos start = origin;
        switch(mop.getFace()) {
            case DOWN:
            case UP:
                // x y depends on the angle we look?
                x = facingVec.getX() * height + facingVec.getZ() * width;
                y = mop.getFace().getAxisDirection().getOffset() * -depth;
                z = facingVec.getX() * width + facingVec.getZ() * height;
                start = start.add(-x / 2, 0, -z / 2);
                if(x % 2 == 0) {
                    if(x > 0 && mop.getHitVec().x - mop.getPos().getX() > 0.5d) {
                        start = start.add(1, 0, 0);
                    }
                    else if(x < 0 && mop.getHitVec().x - mop.getPos().getX() < 0.5d) {
                        start = start.add(-1, 0, 0);
                    }
                }
                if(z % 2 == 0) {
                    if(z > 0 && mop.getHitVec().z - mop.getPos().getZ() > 0.5d) {
                        start = start.add(0, 0, 1);
                    }
                    else if(z < 0 && mop.getHitVec().z - mop.getPos().getZ() < 0.5d) {
                        start = start.add(0, 0, -1);
                    }
                }
                break;
            case NORTH:
            case SOUTH:
                x = width;
                y = height;
                z = mop.getFace().getAxisDirection().getOffset() * -depth;
                start = start.add(-x / 2, -y / 2, 0);
                if(x % 2 == 0 && mop.getHitVec().x - mop.getPos().getX() > 0.5d) {
                    start = start.add(1, 0, 0);
                }
                if(y % 2 == 0 && mop.getHitVec().y - mop.getPos().getY() > 0.5d) {
                    start = start.add(0, 1, 0);
                }
                break;
            case WEST:
            case EAST:
                x = mop.getFace().getAxisDirection().getOffset() * -depth;
                y = height;
                z = width;
                start = start.add(-0, -y / 2, -z / 2);
                if(y % 2 == 0 && mop.getHitVec().y - mop.getPos().getY() > 0.5d) {
                    start = start.add(0, 1, 0);
                }
                if(z % 2 == 0 && mop.getHitVec().z - mop.getPos().getZ() > 0.5d) {
                    start = start.add(0, 0, 1);
                }
                break;
            default:
                x = y = z = 0;
        }

        ArrayList<BlockPos> builder = new ArrayList<>();
        for(int xp = start.getX(); xp != start.getX() + x; xp += x / MathHelper.abs(x)) {
            for(int yp = start.getY(); yp != start.getY() + y; yp += y / MathHelper.abs(y)) {
                for(int zp = start.getZ(); zp != start.getZ() + z; zp += z / MathHelper.abs(z)) {
                    // don't add the origin block
                    if(xp == origin.getX() && yp == origin.getY() && zp == origin.getZ()) {
                        continue;
                    }
                    if(distance > 0 && MathHelper.abs(xp - origin.getX()) + MathHelper.abs(yp - origin.getY()) + MathHelper.abs(
                            zp - origin.getZ()) > distance) {
                        continue;
                    }
                    BlockPos pos = new BlockPos(xp, yp, zp);
                    builder.add(pos);
                }
            }
        }
        builder.add(origin);

        return builder;
    }
}
