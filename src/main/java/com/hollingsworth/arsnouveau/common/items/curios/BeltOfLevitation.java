package com.hollingsworth.arsnouveau.common.items.curios;

import com.hollingsworth.arsnouveau.api.item.ArsNouveauCurio;
import com.hollingsworth.arsnouveau.common.lib.LibItemNames;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.world.World;

public class BeltOfLevitation extends ArsNouveauCurio {
    public BeltOfLevitation() { super(LibItemNames.BELT_OF_LEVITATION); }


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
        }
    }
}
