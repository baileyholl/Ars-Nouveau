package com.hollingsworth.arsnouveau.items.curios;

import com.hollingsworth.arsnouveau.api.item.ArsNouveauCurio;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.world.World;

public class BeltOfLevitation extends ArsNouveauCurio {
    public BeltOfLevitation() { super(); }


    @Override
    public void wearableTick(LivingEntity player) {
        if(player.getEntityWorld().isRemote)
            return;

       

        if(!player.onGround && player.isSneaking()){
            boolean isTooHigh = true;
            World world = player.getEntityWorld();
            for(int i = 1; i < 6; i ++){
                if(world.getBlockState(player.getPosition().down(i)).getMaterial() != Material.AIR) {
                    isTooHigh = false;
                    break;
                }
            }

            if(!isTooHigh) {
                player.addPotionEffect(new EffectInstance(Effects.LEVITATION, 5, 2));
            }else {
                player.addPotionEffect(new EffectInstance(Effects.SLOW_FALLING, 5, 2));
            }
            player.fallDistance = 0.0f;
//            player.setMotion(player.getMotion().getX(), player.getMotion().getY(), player.getMotion().getZ());
//            System.out.println(player.getMotion().getY());
//            player.velocityChanged = true;
        }
    }
}
