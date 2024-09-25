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

public class PacketSyncPlayerCap extends AbstractPacket{
    CompoundTag tag;

    //Decoder
    public PacketSyncPlayerCap(RegistryFriendlyByteBuf buf) {
        tag = buf.readNbt();
    }

    //Encoder
    public void toBytes(RegistryFriendlyByteBuf buf) {
        buf.writeNbt(tag);
    }

    public PacketSyncPlayerCap(CompoundTag famCaps) {
        this.tag = famCaps;
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player playerEntity) {
        ANPlayerData data = new ANPlayerData();
        data.deserializeNBT(playerEntity.registryAccess(), this.tag);
        playerEntity.setData(AttachmentsRegistry.PLAYER_DATA, data);
        var cap = CapabilityRegistry.getPlayerDataCap(ArsNouveau.proxy.getPlayer());
        if(cap != null){
            cap.setPlayerData(data);
        }
    }

    public static final Type<PacketSyncPlayerCap> TYPE = new Type<>(ArsNouveau.prefix("sync_player_cap"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketSyncPlayerCap> CODEC = StreamCodec.ofMember(PacketSyncPlayerCap::toBytes, PacketSyncPlayerCap::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
