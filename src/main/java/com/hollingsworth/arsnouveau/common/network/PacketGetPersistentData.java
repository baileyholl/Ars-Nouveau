package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.client.ClientInfo;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketGetPersistentData {

    public CompoundNBT tag;
    //Decoder
    public PacketGetPersistentData(PacketBuffer buf){
        tag = buf.readNbt();
    }

    //Encoder
    public void toBytes(PacketBuffer buf){
        buf.writeNbt(tag);
    }

    public PacketGetPersistentData(CompoundNBT tag){
        this.tag = tag;
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(()->{
            ClientInfo.persistentData = tag;
        } );
        ctx.get().setPacketHandled(true);
    }
}
