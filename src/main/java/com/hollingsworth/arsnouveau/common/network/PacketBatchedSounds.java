package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PacketBatchedSounds extends AbstractPacket {
    List<ClientboundLevelParticlesPacket> particles;

    public PacketBatchedSounds(List<ClientboundLevelParticlesPacket> particles) {
        this.particles = particles;
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        super.onClientReceived(minecraft, player);

        var connection = minecraft.getConnection();
        if (connection != null) {
            for (ClientboundLevelParticlesPacket particle : particles) {
                particle.handle(connection);
            }
        }
    }

    public static final Type<PacketBatchedSounds> TYPE = new Type<>(ArsNouveau.prefix("batched_particles"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketBatchedSounds> CODEC = StreamCodec.composite(ClientboundLevelParticlesPacket.STREAM_CODEC.apply(ByteBufCodecs.list()), u -> u.particles, PacketBatchedSounds::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
