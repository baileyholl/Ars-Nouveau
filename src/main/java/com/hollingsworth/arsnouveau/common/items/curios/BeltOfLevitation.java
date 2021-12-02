package com.hollingsworth.arsnouveau.common.items.curios;

import com.hollingsworth.arsnouveau.api.item.ArsNouveauCurio;
import com.hollingsworth.arsnouveau.common.lib.LibItemNames;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

public class BeltOfLevitation extends ArsNouveauCurio {
    public BeltOfLevitation() { super(LibItemNames.BELT_OF_LEVITATION); }


    @Override
    public void wearableTick(LivingEntity player) {
        if(player instanceof Player && ((Player) player).abilities.flying){
            return;
        }

        if(!player.isOnGround() && player.isShiftKeyDown() && !player.level.isClientSide){
            boolean isTooHigh = true;
            Level world = player.getCommandSenderWorld();
            for(int i = 1; i < 6; i ++){
                if(world.getBlockState(player.blockPosition().below(i)).getMaterial() != Material.AIR) {
                    isTooHigh = false;
                    break;
                }
            }

            if(!isTooHigh) {
                player.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 5, 2));
            }else {
                player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 5, 2));
            }
            player.fallDistance = 0.0f;
        }
        if(player.level.isClientSide){
            Vec3 oldMotion = player.getDeltaMovement();
            double y = oldMotion.y();
            Vec3 motion = player.getDeltaMovement().scale(1.1);
            if(Math.sqrt(motion.length()) > 0.6){
                return;
            }
            player.lerpMotion(motion.x, y, motion.z);
            player.hurtMarked = true;
        }
    }
}
