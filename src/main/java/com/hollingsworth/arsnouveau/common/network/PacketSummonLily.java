package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.common.entity.Lily;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.NetworkEvent;
import java.util.function.Supplier;

public class PacketSummonLily {


    public PacketSummonLily() {

    }

    //Decoder
    public PacketSummonLily(FriendlyByteBuf buf) {

    }

    //Encoder
    public void toBytes(FriendlyByteBuf buf) {

    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getSender() != null) {
                ServerPlayer player = ctx.get().getSender();
                Lily lily = new Lily(player.level);
                lily.setPos(player.getX(), player.getY(), player.getZ());
                lily.setOwnerUUID(player.getUUID());
                player.level.addFreshEntity(lily);
                Lily.ownerLilyMap.put(player.getUUID(), lily.getUUID());
            }
        });
        ctx.get().setPacketHandled(true);

    }
}
