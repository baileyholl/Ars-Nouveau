package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.particle.ParticleEmitter;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

public class TickEmitterPacket extends AbstractPacket {
    public static final Type<TickEmitterPacket> TYPE = new Type<>(ArsNouveau.prefix("tick_emitter"));
    public static final StreamCodec<RegistryFriendlyByteBuf, TickEmitterPacket> CODEC = StreamCodec.ofMember(TickEmitterPacket::toBytes, TickEmitterPacket::new);

    ParticleEmitter emitter;

    public TickEmitterPacket(ParticleEmitter emitter) {
        this.emitter = emitter;
    }

    public TickEmitterPacket(RegistryFriendlyByteBuf buf) {
        emitter = ParticleEmitter.STREAM.decode(buf);
    }

    public void toBytes(RegistryFriendlyByteBuf buf) {
        ParticleEmitter.STREAM.encode(buf, emitter);
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        super.onClientReceived(minecraft, player);
        emitter.tick(player.level);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

}
