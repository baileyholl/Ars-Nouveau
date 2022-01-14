package com.hollingsworth.arsnouveau.common.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketSetScribeRecipe {
    BlockPos scribePos;
    public PacketSetScribeRecipe(FriendlyByteBuf buf){
        this.scribePos = buf.readBlockPos();
    }

    //Encoder
    public void toBytes(FriendlyByteBuf buf){
        buf.writeBlockPos(scribePos);
    }

    public PacketSetScribeRecipe(BlockPos scribesPos){
        this.scribePos = scribesPos;
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(()-> {

        });
        ctx.get().setPacketHandled(true);
    }
}
