package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.particle.ParticleEmitter;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PacketBatchedTickEmitter extends AbstractPacket {
    List<ParticleEmitter> emitters;

    public PacketBatchedTickEmitter(List<ParticleEmitter> particles) {
        this.emitters = particles;
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        super.onClientReceived(minecraft, player);

        for (var emitter : emitters) {
            emitter.tick(player.level);
        }
    }

    public static final Type<PacketBatchedTickEmitter> TYPE = new Type<>(ArsNouveau.prefix("batched_tick_emitter"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketBatchedTickEmitter> CODEC = StreamCodec.composite(ParticleEmitter.STREAM.apply(ByteBufCodecs.list()), u -> u.emitters, PacketBatchedTickEmitter::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
