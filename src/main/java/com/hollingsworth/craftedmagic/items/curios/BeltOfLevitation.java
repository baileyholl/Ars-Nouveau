package com.hollingsworth.craftedmagic.items.curios;

import com.hollingsworth.craftedmagic.ArsNouveau;
import com.hollingsworth.craftedmagic.api.item.ArsNouveauCurio;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.world.World;
import top.theillusivec4.curios.api.CuriosAPI;
import top.theillusivec4.curios.api.capability.ICurio;

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
