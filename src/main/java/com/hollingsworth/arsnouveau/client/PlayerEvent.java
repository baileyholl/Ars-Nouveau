package com.hollingsworth.arsnouveau.client;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.util.MappingUtil;
import com.hollingsworth.arsnouveau.client.particle.engine.ParticleEngine;
import com.hollingsworth.arsnouveau.common.block.ScribesBlock;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FirstPersonRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;


@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = ArsNouveau.MODID)
public class PlayerEvent {

    private static final Minecraft minecraft = Minecraft.getInstance();

    @SubscribeEvent
    public static void onTick(final TickEvent.RenderTickEvent evt) {
        ParticleEngine.getInstance().tick();
    }


    @SubscribeEvent
    public static void onTick(final TickEvent.PlayerTickEvent evt) {

//        if(evt.side == LogicalSide.CLIENT){
//            World world = evt.player.getEntityWorld();
//            Random rand = evt.player.getRNG();
//            Vec3d particlePos = evt.player.getPositionVec();
//            int roteAngle = ClientInfo.ticksInGame;
//            if(rand.nextInt(5) == 0){
//                for(int i =0; i < 10; i++){
//                    world.addParticle(ParticleSparkleData.createData(new ParticleColor(52,36,255), 0.1f, 120),
//                            particlePos.getX()  + Math.cos(roteAngle)/2 + ParticleUtil.inRange(-0.1, 0.1), particlePos.getY()   +1 + ParticleUtil.inRange(-0.1, 0.1), particlePos.getZ()
//                                    + Math.sin(roteAngle)/2 + ParticleUtil.inRange(-0.1, 0.1),
//                            0, 0,0);
//                }
//
//            }
//        }
    }
    @SubscribeEvent
    public static void onBlock(final PlayerInteractEvent.RightClickBlock event) {
        PlayerEntity entity = event.getPlayer();
        if(!event.getWorld().isRemote || event.getHand() != Hand.MAIN_HAND || event.getWorld().getBlockState(event.getPos()).getBlock() instanceof ScribesBlock)
            return;
        if(entity.getHeldItem(event.getHand()).getItem() instanceof SpellBook){
            event.setCanceled(true);
            ObfuscationReflectionHelper.setPrivateValue(FirstPersonRenderer.class, minecraft.getFirstPersonRenderer(), 1f, MappingUtil.getEquippedProgressMainhand());
        }
    }

    @SubscribeEvent
    public static void onItem(final PlayerInteractEvent.RightClickItem event) {
        PlayerEntity entity = event.getPlayer();
        if(!event.getWorld().isRemote || event.getHand() != Hand.MAIN_HAND)
            return;
        if(entity.getHeldItem(event.getHand()).getItem() instanceof SpellBook){
            event.setCanceled(true);
            ObfuscationReflectionHelper.setPrivateValue(FirstPersonRenderer.class, minecraft.getFirstPersonRenderer(), 1f, MappingUtil.getEquippedProgressMainhand());
        }
    }
}
