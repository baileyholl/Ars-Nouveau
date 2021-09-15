package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.common.entity.pathfinding.pathjobs.AbstractPathJob;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketTogglePathing {



    //Decoder
    public PacketTogglePathing(PacketBuffer buf){
    }

    //Encoder
    public void toBytes(PacketBuffer buf){

    }

    public PacketTogglePathing(){ }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(()-> AbstractPathJob.DEBUG_DRAW = !AbstractPathJob.DEBUG_DRAW);
        ctx.get().setPacketHandled(true);
    }
}
