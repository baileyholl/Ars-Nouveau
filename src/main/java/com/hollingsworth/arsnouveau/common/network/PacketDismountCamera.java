package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.common.entity.ScryerCamera;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.NetworkEvent;
import java.util.function.Supplier;

// https://github.com/Geforce132/SecurityCraft/blob/1.18.2/src/main/java/net/geforcemods/securitycraft/network/server/DismountCamera.java
public class PacketDismountCamera {
    public PacketDismountCamera() {
    }

    public static void encode(PacketDismountCamera message, FriendlyByteBuf buf) {
    }

    public static PacketDismountCamera decode(FriendlyByteBuf buf) {
        return new PacketDismountCamera();
    }

    public static void onMessage(PacketDismountCamera message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();

            if (player.getCamera() instanceof ScryerCamera cam)
                cam.stopViewing(player);
        });

        ctx.get().setPacketHandled(true);
    }
}
