package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

public class PacketUpdateFlight extends AbstractPacket{

    public boolean canFly;
    public boolean wasFlying;

    //Decoder
    public PacketUpdateFlight(RegistryFriendlyByteBuf buf) {
        canFly = buf.readBoolean();
        wasFlying = buf.readBoolean();
    }

    //Encoder
    public void toBytes(RegistryFriendlyByteBuf buf) {
        buf.writeBoolean(canFly);
        buf.writeBoolean(wasFlying);
    }

    public PacketUpdateFlight(boolean canFly) {
        this.canFly = canFly;
    }

    public PacketUpdateFlight(boolean canFly, boolean wasFlying) {
        this.canFly = canFly;
        this.wasFlying = wasFlying;
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        ArsNouveau.proxy.getPlayer().getAbilities().mayfly = canFly;
        ArsNouveau.proxy.getPlayer().getAbilities().flying = wasFlying;
    }
    public static final Type<PacketUpdateFlight> TYPE = new Type<>(ArsNouveau.prefix("update_flight"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketUpdateFlight> CODEC = StreamCodec.ofMember(PacketUpdateFlight::toBytes, PacketUpdateFlight::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
