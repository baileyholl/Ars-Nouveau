package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.common.entity.EntityFollowProjectile;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketBeam {


    private final Vec3d toPos;
    private final Vec3d fromPos;
    private final int duration;

    public PacketBeam(Vec3d from, Vec3d to, int duration) {
        this.fromPos = from;
        this.toPos = to;
        this.duration = duration;
    }



    public static PacketBeam decode(PacketBuffer buf) {
        return new PacketBeam(new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble()), new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble()), buf.readInt());
    }

    public static void encode(PacketBeam msg, PacketBuffer buf) {
        buf.writeDouble(msg.fromPos.x);
        buf.writeDouble(msg.fromPos.y);
        buf.writeDouble(msg.fromPos.z);
        buf.writeDouble(msg.toPos.x);
        buf.writeDouble(msg.toPos.y);
        buf.writeDouble(msg.toPos.z);
        buf.writeInt(msg.duration);
    }

    public static class Handler {
        public static void handle(final PacketBeam message, final Supplier<NetworkEvent.Context> ctx) {
            if (ctx.get().getDirection().getReceptionSide().isServer()) {
                ctx.get().setPacketHandled(true);
                return;
            }
            ctx.get().enqueueWork(new Runnable() {
                // Use anon - lambda causes classloading issues
                @Override
                public void run() {
                    Minecraft.getInstance().world.addEntity(new EntityFollowProjectile(Minecraft.getInstance().world,
                            message.fromPos, message.toPos));
//                    RenderEventQueue.getInstance().addEvent(new BeamEvent(message.fromPos, message.toPos, message.duration));
                };
            });
            ctx.get().setPacketHandled(true);

        }
    }
}
