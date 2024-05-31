package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.common.light.LightManager;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.neoforge.network.NetworkEvent;
import java.util.function.Supplier;

public class PacketToggleLight {
    boolean enabled;

    //Decoder
    public PacketToggleLight(FriendlyByteBuf buf) {
        enabled = buf.readBoolean();
    }

    //Encoder
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBoolean(enabled);
    }

    public PacketToggleLight(boolean stack) {
        this.enabled = stack;
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            LightManager.toggleLightsAndConfig(enabled);
        });
        ctx.get().setPacketHandled(true);
    }

}
