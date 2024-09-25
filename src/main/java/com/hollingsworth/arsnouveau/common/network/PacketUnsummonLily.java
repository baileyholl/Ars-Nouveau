package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.Lily;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import java.util.UUID;

public class PacketUnsummonLily extends AbstractPacket {


    public PacketUnsummonLily() {

    }

    //Decoder
    public PacketUnsummonLily(RegistryFriendlyByteBuf buf) {

    }

    //Encoder
    public void toBytes(RegistryFriendlyByteBuf buf) {

    }

    @Override
    public void onServerReceived(MinecraftServer minecraftServer, ServerPlayer player) {
        ServerLevel level = (ServerLevel) player.level();
        UUID lilyUuid = Lily.ownerLilyMap.get(player.getUUID());
        if (lilyUuid != null) {
            Lily lily = (Lily) level.getEntity(lilyUuid);
            if (lily != null) {
                lily.remove(Entity.RemovalReason.DISCARDED);
            }
        }
    }

    public static final Type<PacketUnsummonLily> TYPE = new Type<>(ArsNouveau.prefix("unsummon_lily"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketUnsummonLily> CODEC = StreamCodec.ofMember(PacketUnsummonLily::toBytes, PacketUnsummonLily::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
