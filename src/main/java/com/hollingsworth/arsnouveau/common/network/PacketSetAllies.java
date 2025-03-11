package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.world.saved_data.AlliesSavedData;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PacketSetAllies extends AbstractPacket {

    public static final Type<PacketSetAllies> TYPE = new Type<>(ArsNouveau.prefix("set_allies"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketSetAllies> CODEC = StreamCodec.ofMember(PacketSetAllies::toBytes, PacketSetAllies::new);

    private final UUID owner;
    private final Set<UUID> allies;

    public PacketSetAllies(UUID player, Set<UUID> allies) {
        this.owner = (player);
        this.allies = allies;
    }

    public PacketSetAllies(RegistryFriendlyByteBuf buf) {
        this.owner = buf.readUUID();
        int size = buf.readInt();
        this.allies = new HashSet<>();
        for (int i = 0; i < size; i++) {
            this.allies.add(buf.readUUID());
        }
    }

    @Override
    void toBytes(RegistryFriendlyByteBuf buf) {
        buf.writeUUID(owner);
        buf.writeInt(allies.size());
        allies.forEach(buf::writeUUID);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void onServerReceived(MinecraftServer minecraftServer, ServerPlayer player) {
        // when received on server, store the allies in the player's data
        AlliesSavedData.setAllies(minecraftServer.overworld(), owner, allies);
        System.out.println("Received allies data for " + player.getUUID() + " with " + allies.size() + " allies");
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        AlliesSavedData.setLocalAllies(allies);
        System.out.println("Received allies data for " + player.getUUID() + " with " + allies.size() + " allies");
    }

}
