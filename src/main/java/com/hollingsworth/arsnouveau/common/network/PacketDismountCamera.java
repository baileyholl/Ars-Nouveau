package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.ICameraCallback;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

// https://github.com/Geforce132/SecurityCraft/blob/1.18.2/src/main/java/net/geforcemods/securitycraft/network/server/DismountCamera.java
public class PacketDismountCamera extends AbstractPacket{
    public static final Type<PacketDismountCamera> TYPE = new Type<>(ArsNouveau.prefix("dismount_camera"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketDismountCamera> CODEC = StreamCodec.ofMember(PacketDismountCamera::toBytes, PacketDismountCamera::new);

    public PacketDismountCamera(){}

    public PacketDismountCamera(RegistryFriendlyByteBuf buf) {}

    @Override
    public void toBytes(RegistryFriendlyByteBuf buf) {}

    @Override
    public void onServerReceived(MinecraftServer minecraftServer, ServerPlayer player) {
        if (player.getCamera() instanceof ICameraCallback cam)
            cam.stopViewing(player);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
