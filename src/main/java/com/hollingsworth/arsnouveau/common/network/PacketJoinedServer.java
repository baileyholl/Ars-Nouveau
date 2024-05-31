package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.setup.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.NetworkEvent;
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
                    if(Config.SHOW_SUPPORTER_MESSAGE.get()) {
                        Minecraft.getInstance().player.sendSystemMessage(Component.translatable("ars_nouveau.rewards.enabled"));
                        Config.SHOW_SUPPORTER_MESSAGE.set(false);
                        Config.SHOW_SUPPORTER_MESSAGE.save();
                    }
                }
            });
            ctx.get().setPacketHandled(true);

        }
    }

}
