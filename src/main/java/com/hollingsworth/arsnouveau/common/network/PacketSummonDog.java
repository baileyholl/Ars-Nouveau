package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.Lily;
import com.hollingsworth.arsnouveau.common.entity.Nook;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class PacketSummonDog extends AbstractPacket {

    public enum DogType {
        NOOK,
        LILY
    }

    public static final Type<PacketSummonDog> TYPE = new Type<>(ArsNouveau.prefix("summon_dog"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketSummonDog> CODEC = StreamCodec.ofMember(PacketSummonDog::toBytes, PacketSummonDog::new);

    public DogType dogType;

    public PacketSummonDog(DogType summonNook) {
        this.dogType = summonNook;
    }

    //Decoder
    public PacketSummonDog(RegistryFriendlyByteBuf buf) {
        this.dogType = buf.readEnum(DogType.class);
    }

    //Encoder
    public void toBytes(RegistryFriendlyByteBuf buf) {
        buf.writeEnum(dogType);
    }

    @Override
    public void onServerReceived(MinecraftServer minecraftServer, ServerPlayer player) {
        if (dogType == DogType.NOOK) {
            Nook nook = new Nook(player.level);
            nook.setPos(player.getX(), player.getY(), player.getZ());
            nook.setOwnerUUID(player.getUUID());
            player.level.addFreshEntity(nook);
            Nook.ownerNookMap.put(player.getUUID(), nook.getUUID());
        } else {
            Lily lily = new Lily(player.level);
            lily.setPos(player.getX(), player.getY(), player.getZ());
            lily.setOwnerUUID(player.getUUID());
            player.level.addFreshEntity(lily);
            Lily.ownerLilyMap.put(player.getUUID(), lily.getUUID());
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
