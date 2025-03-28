package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.container.AbstractStorageTerminalScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class ServerToClientStoragePacket extends AbstractPacket{

    public String searchString;

    public List<String> tabNames;

    public ServerToClientStoragePacket(String searchString, List<String> tabNames) {
        this.searchString = searchString == null ? "" : searchString;
        this.tabNames = tabNames;
    }

    public ServerToClientStoragePacket(RegistryFriendlyByteBuf pb) {
        this.searchString = ByteBufCodecs.STRING_UTF8.decode(pb);
        this.tabNames = ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()).decode(pb);
    }

    public void toBytes(RegistryFriendlyByteBuf pb) {
        ByteBufCodecs.STRING_UTF8.encode(pb, this.searchString);
        ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()).encode(pb, this.tabNames);
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        if (minecraft.screen instanceof AbstractStorageTerminalScreen<?> terminalScreen) {
            terminalScreen.receiveServerSettings(searchString, tabNames);
        }
    }

    public static final Type<ServerToClientStoragePacket> TYPE = new Type<>(ArsNouveau.prefix("server_to_client_storage"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerToClientStoragePacket> CODEC = StreamCodec.ofMember(ServerToClientStoragePacket::toBytes, ServerToClientStoragePacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
