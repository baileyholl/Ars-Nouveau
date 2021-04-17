package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.particle.GlowParticleData;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.engine.ParticleEngine;
import com.hollingsworth.arsnouveau.client.particle.engine.TimedBeam;
import com.hollingsworth.arsnouveau.client.particle.engine.TimedHelix;
import com.hollingsworth.arsnouveau.common.entity.EntityFollowProjectile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketBeam {

    private final Vector3d toPos;
    private final Vector3d fromPos;
    private final int duration;

    public PacketBeam(Vector3d from, Vector3d to, int duration) {
        this.fromPos = from;
        this.toPos = to;
        this.duration = duration;
    }



    public static PacketBeam decode(PacketBuffer buf) {
        return new PacketBeam(new Vector3d(buf.readDouble(), buf.readDouble(), buf.readDouble()), new Vector3d(buf.readDouble(), buf.readDouble(), buf.readDouble()), buf.readInt());
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
                    ArsNouveau.proxy.getMinecraft().level.addFreshEntity(new EntityFollowProjectile(ArsNouveau.proxy.getMinecraft().level,
                            message.fromPos, message.toPos));
//                    RenderEventQueue.getInstance().addEvent(new BeamEvent(message.fromPos, message.toPos, message.duration));
                };
            });
            ctx.get().setPacketHandled(true);

        }
    }
}
