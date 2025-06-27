package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.ClientInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

public class PacketGetPersistentData extends AbstractPacket {
    public static final Type<PacketGetPersistentData> TYPE = new Type<>(ArsNouveau.prefix("get_persistent_data"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketGetPersistentData> CODEC = StreamCodec.ofMember(PacketGetPersistentData::toBytes, PacketGetPersistentData::new);
    public CompoundTag tag;

    //Decoder
    public PacketGetPersistentData(RegistryFriendlyByteBuf buf) {
        tag = buf.readNbt();
    }

    //Encoder
    public void toBytes(RegistryFriendlyByteBuf buf) {
        buf.writeNbt(tag);
    }

    public PacketGetPersistentData(CompoundTag tag) {
        this.tag = tag;
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        ClientInfo.persistentData = tag;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
