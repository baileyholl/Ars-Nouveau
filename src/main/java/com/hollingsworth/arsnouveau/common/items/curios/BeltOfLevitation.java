package com.hollingsworth.arsnouveau.common.items.curios;

import com.hollingsworth.arsnouveau.api.item.ArsNouveauCurio;
import com.hollingsworth.arsnouveau.common.lib.LibItemNames;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class BeltOfLevitation extends ArsNouveauCurio {
    public BeltOfLevitation() { super(LibItemNames.BELT_OF_LEVITATION); }


    @Override
    public void wearableTick(LivingEntity player) {


        if(!player.isOnGround() && player.isShiftKeyDown() && !player.level.isClientSide){
            boolean isTooHigh = true;
            World world = player.getCommandSenderWorld();
            for(int i = 1; i < 6; i ++){
                if(world.getBlockState(player.blockPosition().below(i)).getMaterial() != Material.AIR) {
                    isTooHigh = false;
                    break;
                }
            }

            if(!isTooHigh) {
                player.addEffect(new EffectInstance(Effects.LEVITATION, 5, 2));
            }else {
                player.addEffect(new EffectInstance(Effects.SLOW_FALLING, 5, 2));
            }
            player.fallDistance = 0.0f;
        }
        if(player.level.isClientSide){
            Vector3d oldMotion = player.getDeltaMovement();
            double y = oldMotion.y();
            Vector3d motion = player.getDeltaMovement().scale(1.1);
            if(Math.sqrt(motion.length()) > 0.6){
                return;
            }
            player.lerpMotion(motion.x, y, motion.z);
            player.hurtMarked = true;
        }
    }
}
