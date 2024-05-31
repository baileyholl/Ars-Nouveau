package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.common.entity.pathfinding.pathjobs.AbstractPathJob;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.neoforge.network.NetworkEvent;
import java.util.function.Supplier;

public class PacketTogglePathing {


    //Decoder
    public PacketTogglePathing(FriendlyByteBuf buf) {
    }

    //Encoder
    public void toBytes(FriendlyByteBuf buf) {

    }

    public PacketTogglePathing() {
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> AbstractPathJob.DEBUG_DRAW = !AbstractPathJob.DEBUG_DRAW);
        ctx.get().setPacketHandled(true);
    }
}
