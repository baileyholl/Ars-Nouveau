package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.common.entity.Lily;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.NetworkEvent;
import java.util.UUID;
import java.util.function.Supplier;

public class PacketUnsummonLily {


    public PacketUnsummonLily() {

    }

    //Decoder
    public PacketUnsummonLily(FriendlyByteBuf buf) {

    }

    //Encoder
    public void toBytes(FriendlyByteBuf buf) {

    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getSender() != null) {
                ServerPlayer serverPlayer = ctx.get().getSender();
                ServerLevel level = (ServerLevel) serverPlayer.level();
                UUID lilyUuid = Lily.ownerLilyMap.get(ctx.get().getSender().getUUID());
                if (lilyUuid != null) {
                    Lily lily = (Lily) level.getEntity(lilyUuid);
                    if (lily != null) {
                        lily.remove(Entity.RemovalReason.DISCARDED);
                    }
                }
            }
        });
        ctx.get().setPacketHandled(true);

    }
}
