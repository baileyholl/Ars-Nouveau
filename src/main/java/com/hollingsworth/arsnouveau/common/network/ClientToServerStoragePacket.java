package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.container.StorageTerminalMenu;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class ClientToServerStoragePacket extends AbstractPacket{

    public static final Type<ClientToServerStoragePacket> TYPE = new Type<>(ArsNouveau.prefix("storage_packet"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientToServerStoragePacket> CODEC = StreamCodec.ofMember(ClientToServerStoragePacket::toBytes, ClientToServerStoragePacket::new);

    public CompoundTag tag;

    public ClientToServerStoragePacket(CompoundTag tag) {
        this.tag = tag;
    }

    public ClientToServerStoragePacket(RegistryFriendlyByteBuf pb) {
        tag = pb.readNbt();
    }

    public void toBytes(RegistryFriendlyByteBuf pb) {
        pb.writeNbt(tag);
    }

    @Override
    public void onServerReceived(MinecraftServer minecraftServer, ServerPlayer sender) {
        if (sender.containerMenu instanceof StorageTerminalMenu terminalScreen){
            terminalScreen.receive(sender, minecraftServer.registryAccess(), tag);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
