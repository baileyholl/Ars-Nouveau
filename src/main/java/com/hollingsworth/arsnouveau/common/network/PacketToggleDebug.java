package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class PacketToggleDebug extends AbstractPacket {

    boolean enabled;

    //Decoder
    public PacketToggleDebug(RegistryFriendlyByteBuf buf) {
        enabled = buf.readBoolean();
    }

    //Encoder
    public void toBytes(RegistryFriendlyByteBuf buf) {
        buf.writeBoolean(enabled);
    }

    public PacketToggleDebug(boolean stack) {
        this.enabled = stack;
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        ArsNouveauAPI.ENABLE_DEBUG_NUMBERS = enabled;
    }

    public static final Type<PacketToggleDebug> TYPE = new Type<>(ArsNouveau.prefix("toggle_debug"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketToggleDebug> CODEC = StreamCodec.ofMember(PacketToggleDebug::toBytes, PacketToggleDebug::new);


    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }


}
