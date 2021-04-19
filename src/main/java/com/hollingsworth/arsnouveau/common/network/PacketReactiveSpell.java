package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.common.event.ReactiveEvents;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketReactiveSpell {

    public PacketReactiveSpell(){}


    //Decoder
    public PacketReactiveSpell(PacketBuffer buf){
    }

    //Encoder
    public void toBytes(PacketBuffer buf){}

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(()->{
            ServerPlayerEntity serverPlayerEntity = ctx.get().getSender();
            if(serverPlayerEntity!= null){
                ItemStack stack = serverPlayerEntity.getMainHandItem();
                ReactiveEvents.castSpell(ctx.get().getSender(), stack);
            }
        });
        ctx.get().setPacketHandled(true);

    }
}
