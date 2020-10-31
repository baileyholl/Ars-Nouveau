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
    boolean updateAgain;

    public PacketWarpPosition( Entity entity,double x, double y, double z, boolean updateAgain){
        this.entityID = entity.getEntityId();
        this.x = x;
        this.y = y;
        this.z = z;
        this.updateAgain = updateAgain;
    }

    public PacketWarpPosition( int id,double x, double y, double z, boolean updateAgain){
        this.entityID = id;
        this.x = x;
        this.y = y;
        this.z = z;
        this.updateAgain = updateAgain;
    }

    public static PacketWarpPosition decode(PacketBuffer buf) {
        return new PacketWarpPosition(buf.readInt(),buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readBoolean());
    }

    public static void encode(PacketWarpPosition msg, PacketBuffer buf) {
        buf.writeInt(msg.entityID);
        buf.writeDouble(msg.x);
        buf.writeDouble(msg.y);
        buf.writeDouble(msg.z);
        buf.writeBoolean(msg.updateAgain);
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
                    ClientWorld world = mc.world;
                    Entity e = world.getEntityByID(message.entityID);
                    if(e == null)
                        return;
                    e.setPosition(message.x, message.y, message.z);
                    Networking.sendToNearby(world, e, new PacketWarpPosition(e.getEntityId(), e.getPosX(), e.getPosY(), e.getPosZ(), false));

                }
            });
            ctx.get().setPacketHandled(true);

        }
    }
}
