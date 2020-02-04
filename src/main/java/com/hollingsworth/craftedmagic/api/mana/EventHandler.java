package com.hollingsworth.craftedmagic.api.mana;

import com.hollingsworth.craftedmagic.ArsNouveau;
import com.hollingsworth.craftedmagic.capability.ManaCapability;
import com.hollingsworth.craftedmagic.network.Networking;
import com.hollingsworth.craftedmagic.network.PacketUpdateBookGUI;
import com.hollingsworth.craftedmagic.network.PacketUpdateMana;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = ArsNouveau.MODID)
public class EventHandler {

    @SubscribeEvent
    public static void playerOnTick(TickEvent.PlayerTickEvent e){
        if(e.player instanceof ServerPlayerEntity && !e.player.world.isRemote && e.player.world.getGameTime() % 5 == 0 ){
            ServerPlayerEntity entity = (ServerPlayerEntity) e.player;
            ManaCapability.getMana(entity).ifPresent(mana ->{
                mana.addMana(1);
                Networking.INSTANCE.send(PacketDistributor.PLAYER.with(()->entity), new PacketUpdateMana(mana.getCurrentMana()));
            });
        }
    }

}
