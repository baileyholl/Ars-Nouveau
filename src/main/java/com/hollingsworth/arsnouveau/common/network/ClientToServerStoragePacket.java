package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.client.container.StorageTerminalMenu;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.NetworkDirection;
import net.neoforged.neoforge.network.NetworkEvent;
import java.util.function.Supplier;

public class ClientToServerStoragePacket {
    public CompoundTag tag;

    public ClientToServerStoragePacket(CompoundTag tag) {
        this.tag = tag;
    }

    public ClientToServerStoragePacket(FriendlyByteBuf pb) {
        tag = pb.readAnySizeNbt();
    }

    public void toBytes(FriendlyByteBuf pb) {
        pb.writeNbt(tag);
    }

    public static class Handler {

        @SuppressWarnings("Convert2Lambda")
        public static boolean onMessage(ClientToServerStoragePacket message, Supplier<NetworkEvent.Context> ctx) {
            if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_SERVER) {
                ctx.get().enqueueWork(() -> {
                    ServerPlayer sender = ctx.get().getSender();
                    if (sender.containerMenu instanceof StorageTerminalMenu terminalScreen){
                        terminalScreen.receive(message.tag);
                    }
                });
            }
            ctx.get().setPacketHandled(true);
            return true;
        }
    }
}
