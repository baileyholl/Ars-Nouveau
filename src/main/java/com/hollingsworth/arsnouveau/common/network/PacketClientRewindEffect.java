package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.event.EventQueue;
import com.hollingsworth.arsnouveau.common.event.timed.RewindEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class PacketClientRewindEffect extends AbstractPacket {

    public static final Type<PacketClientRewindEffect> TYPE = new Type<>(ArsNouveau.prefix("rewind_effect"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketClientRewindEffect> CODEC = StreamCodec.ofMember(PacketClientRewindEffect::toBytes, PacketClientRewindEffect::new);

    public int duration;
    public int hitEntityID;

    public PacketClientRewindEffect(RegistryFriendlyByteBuf buf) {
        duration = buf.readInt();
        hitEntityID = buf.readInt();
    }

    public void toBytes(RegistryFriendlyByteBuf buf) {
        buf.writeInt(duration);
        buf.writeInt(hitEntityID);
    }

    public PacketClientRewindEffect(int duration, Entity hitEntity) {
        this.duration = duration;
        this.hitEntityID = hitEntity.getId();
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        Level world = player.level;
        Entity hitEntity = world.getEntity(hitEntityID);
        if (hitEntity != null) {
            EventQueue.getClientQueue().addEvent(new RewindEvent(hitEntity, hitEntity.level.getGameTime(), duration));
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
