package com.hollingsworth.arsnouveau.common.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.NetworkEvent;
import java.util.function.Supplier;

public class PacketWarpPosition {
    private final int entityID;
    double x;
    double y;
    double z;
    float xRot;
    float yRot;

    public PacketWarpPosition(Entity entity, double x, double y, double z) {
        this.entityID = entity.getId();
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public PacketWarpPosition(int id, double x, double y, double z, float xRot, float yRot) {
        this.entityID = id;
        this.x = x;
        this.y = y;
        this.z = z;
        this.xRot = xRot;
        this.yRot = yRot;
    }

    public static PacketWarpPosition decode(FriendlyByteBuf buf) {
        return new PacketWarpPosition(buf.readInt(), buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readFloat(), buf.readFloat());
    }

    public static void encode(PacketWarpPosition msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.entityID);
        buf.writeDouble(msg.x);
        buf.writeDouble(msg.y);
        buf.writeDouble(msg.z);
        buf.writeFloat(msg.xRot);
        buf.writeFloat(msg.yRot);
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
                    ClientLevel world = mc.level;
                    Entity e = world.getEntity(message.entityID);
                    if (e == null)
                        return;
                    e.setPos(message.x, message.y, message.z);
                    e.setXRot(message.xRot);
                    e.setYRot(message.yRot);
                }
            });
            ctx.get().setPacketHandled(true);

        }
    }
}
