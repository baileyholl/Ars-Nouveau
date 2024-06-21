package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.common.items.curios.JumpingRing;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.NetworkEvent;
import java.util.function.Supplier;

public class PacketGenericClientMessage {
    public enum Action{
        JUMP_RING
    }

    Action action;

    public PacketGenericClientMessage(Action action){
        this.action = action;
    }
    //Decoder
    public PacketGenericClientMessage(FriendlyByteBuf buf) {
        this.action = Action.valueOf(buf.readUtf());
    }

    //Encoder
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUtf(action.name());
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                if(action == Action.JUMP_RING){
                    JumpingRing.doJump(player);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }


}
