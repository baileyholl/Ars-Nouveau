package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.light.LightManager;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

public class PacketSyncLitEntities extends AbstractPacket {
    public static final Type<PacketSyncLitEntities> TYPE = new Type<>(ArsNouveau.prefix("sync_lit_entities"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketSyncLitEntities> CODEC = StreamCodec.ofMember(PacketSyncLitEntities::toBytes, PacketSyncLitEntities::new);

    List<Integer> entityIDs = new ArrayList<>();

    //Decoder
    public PacketSyncLitEntities(RegistryFriendlyByteBuf buf) {
        int num = buf.readInt();
        for (int i = 0; i < num; i++) {
            entityIDs.add(buf.readInt());
        }
    }

    //Encoder
    public void toBytes(RegistryFriendlyByteBuf buf) {
        buf.writeInt(entityIDs.size());
        for (Integer i : entityIDs)
            buf.writeInt(i);
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        if (LightManager.shouldUpdateDynamicLight()) {
            LightManager.jarHoldingEntityList = entityIDs;
        }
    }

    public PacketSyncLitEntities(List<Integer> entityIDs) {
        this.entityIDs = entityIDs;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
