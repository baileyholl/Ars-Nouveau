package com.hollingsworth.arsnouveau.common.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class EnchantedFallingBlock extends FallingBlockEntity {
    public EnchantedFallingBlock(EntityType<? extends FallingBlockEntity> p_31950_, Level p_31951_) {
        super(p_31950_, p_31951_);
    }

    public EnchantedFallingBlock(Level world, double v, int y, double v1, BlockState blockState) {
        super(world, v, y, v1, blockState);
    }

    @Override
    public EntityType<?> getType() {
        return ModEntities.FALLING_BLOCK;
    }

    @Override
    public boolean canCollideWith(Entity pEntity) {
        return super.canCollideWith(pEntity) && !(pEntity instanceof FallingBlockEntity);
    }

    @Override
    public void tick() {
        super.tick();

//        if(!level.isClientSide && !didLaunch){
//            didLaunch = true;
//            level.getEntities(null, getBoundingBox().inflate(1.5, 1.5, 1.5)).forEach(entity -> {
//                if(entity instanceof LivingEntity) {
//                    entity.hurt(DamageSource.STALAGMITE, 3);
//                    entity.setDeltaMovement(0, 1.3, 0);
//                }
//            });
//        }
    }
}
