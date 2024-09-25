package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.Lily;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class PacketSummonLily extends AbstractPacket{
    public static final Type<PacketSummonLily> TYPE = new Type<>(ArsNouveau.prefix("summon_lily"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketSummonLily> CODEC = StreamCodec.ofMember(PacketSummonLily::toBytes, PacketSummonLily::new);


    public PacketSummonLily() {

    }

    //Decoder
    public PacketSummonLily(RegistryFriendlyByteBuf buf) {

    }

    //Encoder
    public void toBytes(RegistryFriendlyByteBuf buf) {

    }

    @Override
    public void onServerReceived(MinecraftServer minecraftServer, ServerPlayer player) {
        Lily lily = new Lily(player.level);
        lily.setPos(player.getX(), player.getY(), player.getZ());
        lily.setOwnerUUID(player.getUUID());
        player.level.addFreshEntity(lily);
        Lily.ownerLilyMap.put(player.getUUID(), lily.getUUID());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
