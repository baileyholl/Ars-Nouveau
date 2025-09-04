package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.light.LightManager;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class PacketToggleLight extends AbstractPacket {
    boolean enabled;

    //Decoder
    public PacketToggleLight(RegistryFriendlyByteBuf buf) {
        enabled = buf.readBoolean();
    }

    //Encoder
    public void toBytes(RegistryFriendlyByteBuf buf) {
        buf.writeBoolean(enabled);
    }

    public PacketToggleLight(boolean stack) {
        this.enabled = stack;
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        LightManager.toggleLightsAndConfig(enabled);
    }

    public static final Type<PacketToggleLight> TYPE = new Type<>(ArsNouveau.prefix("toggle_light"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketToggleLight> CODEC = StreamCodec.ofMember(PacketToggleLight::toBytes, PacketToggleLight::new);


    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
