package com.hollingsworth.craftedmagic.items.curios;

import com.hollingsworth.craftedmagic.ArsNouveau;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ArsNouveau.MODID)
public class CuriosEventHandler {

    @SubscribeEvent
    public static void playerOnTick(TickEvent.PlayerTickEvent e) {
        if(e.side.isClient() || e.phase == TickEvent.Phase.START)
            return;
        ServerPlayerEntity player = (ServerPlayerEntity) e.player;
        if(!player.onGround && player.isSneaking()){
            boolean isTooHigh = true;
            World world = player.getServerWorld();
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
