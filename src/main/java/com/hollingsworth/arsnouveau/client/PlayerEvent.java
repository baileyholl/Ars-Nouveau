package com.hollingsworth.arsnouveau.client;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.util.MappingUtil;
import com.hollingsworth.arsnouveau.client.particle.engine.ParticleEngine;
import com.hollingsworth.arsnouveau.common.block.ScribesBlock;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FirstPersonRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
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

    @SubscribeEvent
    public static void onJump(final LivingEvent.LivingJumpEvent event) {
        LivingEntity entity = event.getEntityLiving();
        if(entity == null)
            return;

    }

}
