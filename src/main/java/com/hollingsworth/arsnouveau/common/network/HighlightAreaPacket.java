package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.client.particle.ColorPos;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.neoforge.network.NetworkEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class HighlightAreaPacket {
    public List<ColorPos> colorPos;
    public int ticks;

    public HighlightAreaPacket(List<ColorPos> colorPos, int ticks){
        this.colorPos = colorPos;
        this.ticks = ticks;
    }

    public static HighlightAreaPacket decode(FriendlyByteBuf buf) {
        HighlightAreaPacket packet = new HighlightAreaPacket(new ArrayList<>(), 0);
        int size = buf.readInt();
        for(int i = 0; i < size; i++){
            packet.colorPos.add(ColorPos.fromTag(buf.readNbt()));
        }
        packet.ticks = buf.readInt();
        return packet;
    }

    public static void encode(HighlightAreaPacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.colorPos.size());
        for(ColorPos pos : msg.colorPos){
            buf.writeNbt(pos.toTag());
        }
        buf.writeInt(msg.ticks);
    }

    public static class Handler {
        public static void handle(final HighlightAreaPacket m, final Supplier<NetworkEvent.Context> ctx) {
            if (ctx.get().getDirection().getReceptionSide().isServer()) {
                ctx.get().setPacketHandled(true);
                return;
            }

            ctx.get().enqueueWork(new Runnable() {
                // Use anon - lambda causes classloading issues
                @Override
                public void run() {
                    ClientInfo.highlightPosition(m.colorPos, m.ticks);
                }
            });
            ctx.get().setPacketHandled(true);

        }
    }
}
