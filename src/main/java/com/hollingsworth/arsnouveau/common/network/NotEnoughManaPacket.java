package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.ClientInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

public class NotEnoughManaPacket extends AbstractPacket{
    public static final Type<NotEnoughManaPacket> TYPE = new Type<>(ArsNouveau.prefix("not_enough_mana"));

    public static final StreamCodec<RegistryFriendlyByteBuf, NotEnoughManaPacket> CODEC = StreamCodec.ofMember(NotEnoughManaPacket::toBytes, NotEnoughManaPacket::new);

    int totalCost;

    public NotEnoughManaPacket(int totalCost) {
        this.totalCost = totalCost;
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        ClientInfo.redOverlayTicks = 35;
        ClientInfo.redOverlayMana = totalCost;
    }

    public static void encode(NotEnoughManaPacket msg, RegistryFriendlyByteBuf RegistryFriendlyByteBuf) {
        RegistryFriendlyByteBuf.writeInt(msg.totalCost);
    }

    public NotEnoughManaPacket(RegistryFriendlyByteBuf RegistryFriendlyByteBuf) {
        totalCost = RegistryFriendlyByteBuf.readInt();
    }

    @Override
    public void toBytes(RegistryFriendlyByteBuf buf) {
        buf.writeInt(totalCost);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
