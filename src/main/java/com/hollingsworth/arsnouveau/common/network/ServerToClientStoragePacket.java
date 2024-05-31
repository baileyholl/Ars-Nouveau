package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.client.container.AbstractStorageTerminalScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.neoforge.network.NetworkDirection;
import net.neoforged.neoforge.network.NetworkEvent;
import java.util.function.Supplier;

public class ServerToClientStoragePacket {
    public CompoundTag tag;

    public ServerToClientStoragePacket(CompoundTag tag) {
        this.tag = tag;
    }

    public ServerToClientStoragePacket(FriendlyByteBuf pb) {
        tag = pb.readAnySizeNbt();
    }

    public void toBytes(FriendlyByteBuf pb) {
        pb.writeNbt(tag);
    }

    public static class Handler {

        @SuppressWarnings("Convert2Lambda")
        public static boolean onMessage(ServerToClientStoragePacket message, Supplier<NetworkEvent.Context> ctx) {
            if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
                ctx.get().enqueueWork(() -> {
                    if (Minecraft.getInstance().screen instanceof AbstractStorageTerminalScreen<?> terminalScreen) {
                        terminalScreen.receive(message.tag);
                    }
                });
            }
            ctx.get().setPacketHandled(true);
            return true;
        }
    }
}
