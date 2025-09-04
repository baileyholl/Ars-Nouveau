package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.setup.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

public class PacketJoinedServer extends AbstractPacket {
    public static final Type<PacketJoinedServer> TYPE = new Type<>(ArsNouveau.prefix("joined_server"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketJoinedServer> CODEC = StreamCodec.ofMember(PacketJoinedServer::toBytes, PacketJoinedServer::new);

    public boolean isSupporter;

    public PacketJoinedServer(boolean isSupporter) {
        this.isSupporter = isSupporter;
    }

    public PacketJoinedServer(RegistryFriendlyByteBuf buf) {
        this.isSupporter = buf.readBoolean();
    }

    public void toBytes(RegistryFriendlyByteBuf buf) {
        buf.writeBoolean(isSupporter);
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        ClientInfo.isSupporter = isSupporter;
        if (Config.SHOW_SUPPORTER_MESSAGE.get()) {
            Minecraft.getInstance().player.sendSystemMessage(Component.translatable("ars_nouveau.rewards.enabled"));
            Config.SHOW_SUPPORTER_MESSAGE.set(false);
            Config.SHOW_SUPPORTER_MESSAGE.save();
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
