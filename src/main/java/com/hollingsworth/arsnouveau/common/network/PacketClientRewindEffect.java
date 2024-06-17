package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.event.EventQueue;
import com.hollingsworth.arsnouveau.common.event.timed.RewindEvent;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketClientRewindEffect {

    public int duration;
    public int hitEntityID;

    public PacketClientRewindEffect(FriendlyByteBuf buf) {
        duration = buf.readInt();
        hitEntityID = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(duration);
        buf.writeInt(hitEntityID);
    }

    public PacketClientRewindEffect(int duration, Entity hitEntity) {
        this.duration = duration;
        this.hitEntityID = hitEntity.getId();
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Level world = ArsNouveau.proxy.getClientWorld();
            Entity hitEntity = world.getEntity(hitEntityID);
            if(hitEntity != null) {
                EventQueue.getClientQueue().addEvent(new RewindEvent(hitEntity, hitEntity.level.getGameTime(), duration));
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
