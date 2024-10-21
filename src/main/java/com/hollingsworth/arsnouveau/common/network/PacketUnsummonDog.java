package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.Lily;
import com.hollingsworth.arsnouveau.common.entity.Nook;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import java.util.UUID;

public class PacketUnsummonDog extends AbstractPacket {

    public PacketSummonDog.DogType dogType;

    public PacketUnsummonDog(PacketSummonDog.DogType dogType) {
        this.dogType = dogType;
    }

    //Decoder
    public PacketUnsummonDog(RegistryFriendlyByteBuf buf) {
        this.dogType = buf.readEnum(PacketSummonDog.DogType.class);
    }

    //Encoder
    public void toBytes(RegistryFriendlyByteBuf buf) {
        buf.writeEnum(dogType);
    }

    @Override
    public void onServerReceived(MinecraftServer minecraftServer, ServerPlayer player) {
        if(dogType == PacketSummonDog.DogType.NOOK){
            ServerLevel level = (ServerLevel) player.level();
            UUID nookUuid = Nook.ownerNookMap.get(player.getUUID());
            if (nookUuid != null) {
                Nook nook = (Nook) level.getEntity(nookUuid);
                if (nook != null) {
                    nook.remove(Entity.RemovalReason.DISCARDED);
                }
            }
        }else {
            ServerLevel level = (ServerLevel) player.level();
            UUID lilyUuid = Lily.ownerLilyMap.get(player.getUUID());
            if (lilyUuid != null) {
                Lily lily = (Lily) level.getEntity(lilyUuid);
                if (lily != null) {
                    lily.remove(Entity.RemovalReason.DISCARDED);
                }
            }
        }
    }

    public static final Type<PacketUnsummonDog> TYPE = new Type<>(ArsNouveau.prefix("unsummon_dog"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketUnsummonDog> CODEC = StreamCodec.ofMember(PacketUnsummonDog::toBytes, PacketUnsummonDog::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
