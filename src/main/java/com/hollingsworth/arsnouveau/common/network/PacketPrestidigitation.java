package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.event.EventQueue;
import com.hollingsworth.arsnouveau.api.event.ParticleTimelineEvent;
import com.hollingsworth.arsnouveau.api.particle.ParticleEmitter;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class PacketPrestidigitation extends AbstractPacket{

    int entityId;
    int ticks;
    ParticleEmitter emitter;

    public PacketPrestidigitation(Entity entity, int ticks, ParticleEmitter emitter) {
        this.entityId = entity.getId();
        this.ticks = ticks;
        this.emitter = emitter;
    }

    public PacketPrestidigitation(RegistryFriendlyByteBuf pb) {
        this.entityId = pb.readVarInt();
        this.ticks = pb.readVarInt();
        this.emitter = ParticleEmitter.STREAM.decode(pb);
    }

    public void toBytes(RegistryFriendlyByteBuf pb) {
        pb.writeVarInt(this.entityId);
        pb.writeVarInt(this.ticks);
        ParticleEmitter.STREAM.encode(pb, this.emitter);
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        if(player.level.getEntity(this.entityId) instanceof Entity entity) {
            emitter.position = () -> entity.getBoundingBox().getCenter();
            EventQueue.getClientQueue().addEvent(new ParticleTimelineEvent(player.level, emitter, ticks));
        }
    }

    public static final Type<PacketPrestidigitation> TYPE = new Type<>(ArsNouveau.prefix("prestidigitation"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketPrestidigitation> CODEC = StreamCodec.ofMember(PacketPrestidigitation::toBytes, PacketPrestidigitation::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
