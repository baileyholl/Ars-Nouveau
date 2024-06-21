package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.client.ClientInfo;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.neoforge.network.NetworkEvent;
import java.util.function.Supplier;

public class NotEnoughManaPacket{

    int totalCost;
    public NotEnoughManaPacket(int totalCost) {
        this.totalCost = totalCost;
    }

    public void handle(Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        // This packet is only registered to be received on the client
        if (ctx.getDirection().getReceptionSide().isClient()) {
            ctx.enqueueWork(() -> {
                ClientInfo.redOverlayTicks = 35;
                ClientInfo.redOverlayMana = totalCost;
            });
        }
        ctx.setPacketHandled(true);
    }

    public static void encode(NotEnoughManaPacket msg, FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeInt(msg.totalCost);
    }

    public static NotEnoughManaPacket decode(FriendlyByteBuf friendlyByteBuf) {
        int totalCost = friendlyByteBuf.readInt();
        return new NotEnoughManaPacket(totalCost);
    }

}
