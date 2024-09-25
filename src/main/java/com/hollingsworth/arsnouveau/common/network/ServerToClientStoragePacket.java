package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.container.AbstractStorageTerminalScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

public class ServerToClientStoragePacket extends AbstractPacket{
    public CompoundTag tag;

    public ServerToClientStoragePacket(CompoundTag tag) {
        this.tag = tag;
    }

    public ServerToClientStoragePacket(RegistryFriendlyByteBuf pb) {
        tag = pb.readNbt();
    }

    public void toBytes(RegistryFriendlyByteBuf pb) {
        pb.writeNbt(tag);
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        if (minecraft.screen instanceof AbstractStorageTerminalScreen<?> terminalScreen) {
            terminalScreen.receive(tag);
        }
    }

    public static final Type<ServerToClientStoragePacket> TYPE = new Type<>(ArsNouveau.prefix("server_to_client_storage"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerToClientStoragePacket> CODEC = StreamCodec.ofMember(ServerToClientStoragePacket::toBytes, ServerToClientStoragePacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
