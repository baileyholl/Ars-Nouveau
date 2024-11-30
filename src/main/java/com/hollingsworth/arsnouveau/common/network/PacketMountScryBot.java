package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.ScryBot;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

public class PacketMountScryBot extends AbstractPacket{
    public static final Type<PacketMountScryBot> TYPE = new Type<>(ArsNouveau.prefix("mount_scrybot"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketMountScryBot> CODEC = StreamCodec.ofMember(PacketMountScryBot::encode, PacketMountScryBot::decode);
    private int id;

    public PacketMountScryBot() {
    }

    @Override
    public void toBytes(RegistryFriendlyByteBuf buf) {

    }

    public PacketMountScryBot(int id) {
        this.id = id;
    }

    public static void encode(PacketMountScryBot message, RegistryFriendlyByteBuf buf) {
        buf.writeInt(message.id);
    }

    public static PacketMountScryBot decode(RegistryFriendlyByteBuf buf) {
        return new PacketMountScryBot(buf.readInt());
    }

    @Override
    public void onServerReceived(MinecraftServer minecraftServer, ServerPlayer player) {
        Level level = player.level;

        if (level.getEntity(id) instanceof ScryBot scryBot) {
            scryBot.playerMounting(player);
            return;
        }
        PortUtil.sendMessage(player, Component.translatable("ars_nouveau.camera.not_loaded"));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
