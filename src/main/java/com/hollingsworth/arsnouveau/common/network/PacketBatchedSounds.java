package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PacketBatchedSounds extends AbstractPacket {
    List<ClientboundSoundPacket> sounds;

    public PacketBatchedSounds(List<ClientboundSoundPacket> sounds) {
        this.sounds = sounds;
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        super.onClientReceived(minecraft, player);

        var connection = minecraft.getConnection();
        if (connection != null) {
            for (ClientboundSoundPacket particle : sounds) {
                particle.handle(connection);
            }
        }
    }

    public static final Type<PacketBatchedSounds> TYPE = new Type<>(ArsNouveau.prefix("batched_sounds"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketBatchedSounds> CODEC = StreamCodec.composite(ClientboundSoundPacket.STREAM_CODEC.apply(ByteBufCodecs.list()), u -> u.sounds, PacketBatchedSounds::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
