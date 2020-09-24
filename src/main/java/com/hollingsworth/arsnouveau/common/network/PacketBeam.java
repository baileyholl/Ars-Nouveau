package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.client.particle.GlowParticleData;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleLineData;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
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
                    BlockPos pos = message.fromPos;
                    ParticleEngine.getInstance().addEffect(new TimedBeam(message.fromPos, message.toPos, message.delay, world));
//                    for(int i =0; i< 5; i++){
//                        world.addParticle(ParticleLineData.createData(new ParticleColor(255,25,155)),pos.getX() +0.5 + ParticleUtil.inRange(-0.5, 0.5)  , pos.getY() +0.5  + ParticleUtil.inRange(-0.5, 0.5) , pos.getZ()  +0.5+ ParticleUtil.inRange(-0.5, 0.5),
//                                pos.getX() +0.5 , pos.getY()  +0.5 , pos.getZ() +0.5);
//                    }

                };
            });
            ctx.get().setPacketHandled(true);

        }
    }
}
