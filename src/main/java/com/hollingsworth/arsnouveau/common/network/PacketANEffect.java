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
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketANEffect {

    private final EffectType type;
    private final double x;
    private final double y;
    private final double z;
    private final int[] args;

    public PacketANEffect(EffectType type, double x, double y, double z, int... args) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.z = z;
        this.args = args;
    }

    public PacketANEffect(EffectType type, BlockPos pos, int... args){
        this(type, pos.getX(), pos.getY(), pos.getZ(), args);
    }

    public static PacketANEffect decode(PacketBuffer buf) {
        EffectType type = EffectType.values()[buf.readByte()];
        double x = buf.readDouble();
        double y = buf.readDouble();
        double z = buf.readDouble();
        int[] args = new int[type.argCount];

        for (int i = 0; i < args.length; i++) {
            args[i] = buf.readVarInt();
        }
        return new PacketANEffect(type, x, y, z, args);
    }

    public static void encode(PacketANEffect msg, PacketBuffer buf) {
        buf.writeByte(msg.type.ordinal());
        buf.writeDouble(msg.x);
        buf.writeDouble(msg.y);
        buf.writeDouble(msg.z);

        for (int i = 0; i < msg.type.argCount; i++) {
            buf.writeVarInt(msg.args[i]);
        }
    }

    public static class Handler {
        public static void handle(final PacketANEffect message, final Supplier<NetworkEvent.Context> ctx) {
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
                    switch (message.type){
                        case TIMED_GLOW:{
                            BlockPos fromPos = new BlockPos(message.x + 0.5, message.y + 0.5, message.z + 0.5);
                            BlockPos destPos = new BlockPos(message.args[0], message.args[1],message.args[2]);
                            int delay = message.args[3];
                            System.out.println("sending to " + fromPos.toString());
                            System.out.println("sending from " + destPos.toString());

                            ParticleEngine.getInstance().addEffect(new TimedBeam(fromPos, destPos, delay, world));
                            break;
                        }
                        case TIMED_HELIX:{

                            ParticleEngine.getInstance().addEffect(new TimedHelix(new BlockPos(message.x, message.y - 1, message.z), 3, GlowParticleData.createData(new ParticleColor(255,25,180)), world));
                            break;
                        }
                        case BURST:{
                            for(int i =0; i < 10; i++){
                                double d0 = message.x +0.5; //+ world.rand.nextFloat();
                                double d1 = message.y +1.2;//+ world.rand.nextFloat() ;
                                double d2 = message.z +.5 ; //+ world.rand.nextFloat();
                                world.addParticle(GlowParticleData.createData(new ParticleColor(255,25,180)),d0, d1, d2, (world.rand.nextFloat() * 1 - 0.5)/3, (world.rand.nextFloat() * 1 - 0.5)/3, (world.rand.nextFloat() * 1 - 0.5)/3);
                            }
                            break;
                        }
                    }

                };
            });
            ctx.get().setPacketHandled(true);

        }
    }
    public enum EffectType {
        TIMED_GLOW(4), //dest xyz num_particles
        TIMED_HELIX(0),
        BURST(0)
        ;

        private final int argCount;

        EffectType(int argCount) {
            this.argCount = argCount;
        }
    }
}
