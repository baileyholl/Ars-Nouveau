package com.hollingsworth.arsnouveau.common.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketWarpPosition {
    private final int entityID;
    double x;
    double y;
    double z;
    public PacketWarpPosition( Entity entity,double x, double y, double z){
        this.entityID = entity.getId();
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public PacketWarpPosition( int id,double x, double y, double z){
        this.entityID = id;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static PacketWarpPosition decode(PacketBuffer buf) {
        return new PacketWarpPosition(buf.readInt(),buf.readDouble(), buf.readDouble(), buf.readDouble());
    }

    public static void encode(PacketWarpPosition msg, PacketBuffer buf) {
        buf.writeInt(msg.entityID);
        buf.writeDouble(msg.x);
        buf.writeDouble(msg.y);
        buf.writeDouble(msg.z);
    }
    public static class Handler {
        public static void handle(final PacketWarpPosition message, final Supplier<NetworkEvent.Context> ctx) {
            if (ctx.get().getDirection().getReceptionSide().isServer()) {
                ctx.get().setPacketHandled(true);
                return;
            }

            ctx.get().enqueueWork(new Runnable() {
                // Use anon - lambda causes classloading issues
                @Override
                public void run() {
                    Minecraft mc = Minecraft.getInstance();
                    ClientWorld world = mc.level;
                    Entity e = world.getEntity(message.entityID);
                    if(e == null)
                        return;
                    e.setPos(message.x, message.y, message.z);
                }
            });
            ctx.get().setPacketHandled(true);

        }
    }
}
