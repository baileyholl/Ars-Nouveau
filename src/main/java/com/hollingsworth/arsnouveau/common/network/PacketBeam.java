package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.client.particle.GlowParticleData;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.engine.ParticleEngine;
import com.hollingsworth.arsnouveau.client.particle.engine.TimedBeam;
import com.hollingsworth.arsnouveau.client.particle.engine.TimedHelix;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketBeam {


    private final BlockPos toPos;
    private final BlockPos fromPos;
    private final int delay;

    public PacketBeam(BlockPos from, BlockPos to, int delay) {
        this.fromPos = from;
        this.toPos = to;
        this.delay = delay;
    }



    public static PacketBeam decode(PacketBuffer buf) {
        return new PacketBeam(buf.readBlockPos(), buf.readBlockPos(), buf.readInt());
    }

    public static void encode(PacketBeam msg, PacketBuffer buf) {
        buf.writeBlockPos(msg.fromPos);
        buf.writeBlockPos(msg.toPos);
        buf.writeInt(msg.delay);
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
                    Minecraft mc = Minecraft.getInstance();
                    ClientWorld world = mc.world;
                    ParticleEngine.getInstance().addEffect(new TimedBeam(message.fromPos, message.toPos, message.delay, world));
                };
            });
            ctx.get().setPacketHandled(true);

        }
    }
}
