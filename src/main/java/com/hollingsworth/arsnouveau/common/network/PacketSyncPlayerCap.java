package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.capability.ANPlayerData;
import com.hollingsworth.arsnouveau.setup.registry.AttachmentsRegistry;
import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

public class PacketSyncPlayerCap extends AbstractPacket {
    ANPlayerData data;

    public PacketSyncPlayerCap(ANPlayerData data) {
        this.data = data;
    }

    //Decoder
    public PacketSyncPlayerCap(RegistryFriendlyByteBuf buf) {
        this.data = CODEC.decode(buf).data;
    }

    //Encoder
    public void toBytes(RegistryFriendlyByteBuf buf) {
        CODEC.encode(buf, this);
    }

    @Deprecated(forRemoval = true)
    public PacketSyncPlayerCap(CompoundTag famCaps) {
        this.data = new ANPlayerData();
        this.data.deserializeNBT(null, famCaps);
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player playerEntity) {
        playerEntity.setData(AttachmentsRegistry.PLAYER_DATA, this.data);
        var cap = CapabilityRegistry.getPlayerDataCap(ArsNouveau.proxy.getPlayer());
        if (cap != null) {
            cap.setPlayerData(this.data);
        }
    }

    public static final Type<PacketSyncPlayerCap> TYPE = new Type<>(ArsNouveau.prefix("sync_player_cap"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketSyncPlayerCap> CODEC = StreamCodec.composite(
            ANPlayerData.STREAM_CODEC, p -> p.data,
            PacketSyncPlayerCap::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
