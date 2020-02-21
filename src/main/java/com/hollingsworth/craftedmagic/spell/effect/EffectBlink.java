package com.hollingsworth.craftedmagic.spell.effect;

import com.hollingsworth.craftedmagic.ModConfig;
import com.hollingsworth.craftedmagic.api.spell.AbstractEffect;
import com.hollingsworth.craftedmagic.api.spell.AbstractAugment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

import java.util.ArrayList;

public class EffectBlink extends AbstractEffect {

    public EffectBlink() {
        super(ModConfig.EffectBlinkID, "Blink");
    }
    
    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, LivingEntity shooter, ArrayList<AbstractAugment> augments) {
        if(rayTraceResult instanceof EntityRayTraceResult && ((EntityRayTraceResult) rayTraceResult).getEntity().equals(shooter)) {
            Direction facing = shooter.getAdjustedHorizontalFacing();
            double distance = 8.0f + 3.0f *getAmplificationBonus(augments);
            //BlockPos pos = shooter.getPosition().offset(shooter.getHorizontalFacing(), 3);

            Vec3d lookVec = new Vec3d(shooter.getLookVec().getX(), 0, shooter.getLookVec().getZ());
            Vec3d vec = shooter.getPositionVec().add(lookVec.scale(distance));
            BlockPos pos = new BlockPos(vec);
            if (!isValidTeleport(world, pos)){
                for(double i = distance; i >= 0; i--){
                    vec = shooter.getPositionVec().add(lookVec.scale(i));
                    pos = new BlockPos(vec);

                    if(i <= 0){
                        return;
                    }
                    if (isValidTeleport(world, pos)){
                        break;
                    }

                }

            }
            shooter.setPositionAndUpdate(vec.getX(), vec.getY(), vec.getZ());
        }else if(rayTraceResult instanceof BlockRayTraceResult){
            Vec3d vec = rayTraceResult.getHitVec();
            if(isValidTeleport(world, new BlockPos(vec))){
                shooter.setPositionAndUpdate(vec.getX(), vec.getY(), vec.getZ());
            }
        }
    }

    @Override
    public boolean dampenIsAllowed() {
        return true;
    }

    /**
     * Checks is a player can be placed at a given position without suffocating.
     */
    public static boolean isValidTeleport(World world, BlockPos pos){
        return !world.getBlockState(pos).isSolid() &&  !world.getBlockState(pos.up()).isSolid() && !world.getBlockState(pos.up(2)).isSolid();
    }

    @Override
    public int getManaCost() {
        return 50;
    }

    @Override
    public Tier getTier() {
        return Tier.THREE;
    }
}
