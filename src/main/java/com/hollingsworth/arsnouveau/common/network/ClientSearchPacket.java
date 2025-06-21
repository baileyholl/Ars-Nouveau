package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.container.StorageTerminalMenu;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class ClientSearchPacket extends AbstractPacket {

    public static final Type<ClientSearchPacket> TYPE = new Type<>(ArsNouveau.prefix("storage_search_packet"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientSearchPacket> CODEC = StreamCodec.ofMember(ClientSearchPacket::toBytes, ClientSearchPacket::new);

    public String string;

    public ClientSearchPacket(String searchString) {
        this.string = searchString;
    }

    public ClientSearchPacket(RegistryFriendlyByteBuf pb) {
        this.string = ByteBufCodecs.STRING_UTF8.decode(pb);
    }

    public void toBytes(RegistryFriendlyByteBuf pb) {
        ByteBufCodecs.STRING_UTF8.encode(pb, this.string);
    }

    @Override
    public void onServerReceived(MinecraftServer minecraftServer, ServerPlayer sender) {
        if (sender.containerMenu instanceof StorageTerminalMenu terminalScreen) {
            terminalScreen.receiveClientSearch(sender, string);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
