package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class ArcaneRoad extends ModBlock{
    private static final AxisAlignedBB AABB = new AxisAlignedBB(0F, 0F, 0F, 1F, .8F, 1F);
    public ArcaneRoad(){
        super(LibBlockNames.ARCANE_ROAD);
    }

    @Override
    public void onEntityCollision(BlockState p_196262_1_, World p_196262_2_, BlockPos p_196262_3_, Entity entity) {
        Vector3d dir = entity.getMotion();

        dir = dir.normalize();
        entity.addVelocity(dir.x * 5, dir.y * 5, dir.z * 5);
        super.onEntityCollision(p_196262_1_, p_196262_2_, p_196262_3_, entity);
    }

    @Override
    public void onEntityWalk(World world, BlockPos p_176199_2_, Entity entity) {
        System.out.println("Walking");
        if(world.isRemote){
            Vector3d motion = entity.getMotion().scale(1.5);

            entity.setVelocity(motion.x, motion.y, motion.z);

        }
//        entity.velocityChanged = true;
        super.onEntityWalk(world, p_176199_2_, entity);
    }

    public static AxisAlignedBB getAABB() {
        return AABB;
    }
}
