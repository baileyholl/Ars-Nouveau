package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.client.ClientInfo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketJoinedServer {
    public boolean isSupporter;
    public PacketJoinedServer(boolean isSupporter){
        this.isSupporter = isSupporter;
    }

    public PacketJoinedServer(FriendlyByteBuf buf) {
        this.isSupporter = buf.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBoolean(isSupporter);
    }

    public static class Handler {
        public static void handle(final PacketJoinedServer message, final Supplier<NetworkEvent.Context> ctx) {
            if (ctx.get().getDirection().getReceptionSide().isServer()) {
                ctx.get().setPacketHandled(true);
                return;
            }

            ctx.get().enqueueWork(new Runnable() {
                // Use anon - lambda causes classloading issues
                @Override
                public void run() {
                    ClientInfo.isSupporter = message.isSupporter;
                }
            });
            ctx.get().setPacketHandled(true);

        }
    }

}
