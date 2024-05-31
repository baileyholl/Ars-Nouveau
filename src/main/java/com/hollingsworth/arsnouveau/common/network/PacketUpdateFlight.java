package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.neoforge.network.NetworkEvent;
import java.util.function.Supplier;

public class PacketUpdateFlight {

    public boolean canFly;
    public boolean wasFlying;

    //Decoder
    public PacketUpdateFlight(FriendlyByteBuf buf) {
        canFly = buf.readBoolean();
        wasFlying = buf.readBoolean();
    }

    //Encoder
    public void toBytes(FriendlyByteBuf buf) {
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

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ArsNouveau.proxy.getPlayer().getAbilities().mayfly = canFly;
            ArsNouveau.proxy.getPlayer().getAbilities().flying = wasFlying;
        });
        ctx.get().setPacketHandled(true);
    }

}
