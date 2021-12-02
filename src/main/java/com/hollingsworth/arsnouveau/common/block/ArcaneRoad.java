package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

public class ArcaneRoad extends ModBlock{
    private static final AABB AABB = new AABB(0F, 0F, 0F, 1F, .8F, 1F);
    public ArcaneRoad(){
        super(LibBlockNames.ARCANE_ROAD);
    }

    @Override
    public void entityInside(BlockState p_196262_1_, Level p_196262_2_, BlockPos p_196262_3_, Entity entity) {
        Vec3 dir = entity.getDeltaMovement();

        dir = dir.normalize();
        entity.push(dir.x * 5, dir.y * 5, dir.z * 5);
        super.entityInside(p_196262_1_, p_196262_2_, p_196262_3_, entity);
    }

    @Override
    public void stepOn(Level world, BlockPos p_176199_2_, Entity entity) {
        System.out.println("Walking");
        if(world.isClientSide){
            Vec3 motion = entity.getDeltaMovement().scale(1.5);

            entity.lerpMotion(motion.x, motion.y, motion.z);

        }
//        entity.velocityChanged = true;
        super.stepOn(world, p_176199_2_, entity);
    }

    public static AABB getAABB() {
        return AABB;
    }
}
