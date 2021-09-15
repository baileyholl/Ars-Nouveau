package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.client.particle.GlowParticleData;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketANEffect {

    private final EffectType type;
    private final double x;
    private final double y;
    private final double z;
    private final int red;
    private final int green;
    private final int blue;

    private final int[] args;

    public PacketANEffect(EffectType type, double x, double y, double z, int... args) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.z = z;
        //255,25,180
        this.red = 255;
        this.green = 25;
        this.blue = 180;
        this.args = args;
    }
    public PacketANEffect(EffectType type, double x, double y, double z,ParticleColor.IntWrapper wrapper, int... args) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.z = z;
        this.red = wrapper.r;
        this.blue = wrapper.b;
        this.green = wrapper.g;
        this.args = args;
    }


    public PacketANEffect(EffectType type, BlockPos pos, int... args){
        this(type, pos.getX(), pos.getY(), pos.getZ(), args);
    }

    public PacketANEffect(EffectType type, BlockPos pos, ParticleColor.IntWrapper wrapper, int... args){
        this(type, pos.getX(), pos.getY(), pos.getZ(),wrapper, args);
    }

    public static PacketANEffect decode(PacketBuffer buf) {
        EffectType type = EffectType.values()[buf.readByte()];
        double x = buf.readDouble();
        double y = buf.readDouble();
        double z = buf.readDouble();
        int red = buf.readInt();
        int green = buf.readInt();
        int blue = buf.readInt();
        int[] args = new int[type.argCount];

        for (int i = 0; i < args.length; i++) {
            args[i] = buf.readVarInt();
        }
        return new PacketANEffect(type, x, y, z,new ParticleColor.IntWrapper(red,green,blue), args);
    }

    public static void encode(PacketANEffect msg, PacketBuffer buf) {
        buf.writeByte(msg.type.ordinal());
        buf.writeDouble(msg.x);
        buf.writeDouble(msg.y);
        buf.writeDouble(msg.z);
        buf.writeInt(msg.red);
        buf.writeInt(msg.green);
        buf.writeInt(msg.blue);
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
                    ClientWorld world = mc.level;
                    switch (message.type){

                        case BURST:{
                            for(int i =0; i < 10; i++){
                                double d0 = message.x +0.5; //+ world.rand.nextFloat();
                                double d1 = message.y +1.2;//+ world.rand.nextFloat() ;
                                double d2 = message.z +.5 ; //+ world.rand.nextFloat();
                                world.addParticle(GlowParticleData.createData(new ParticleColor(message.red, message.green, message.blue)),d0, d1, d2, (world.random.nextFloat() * 1 - 0.5)/3, (world.random.nextFloat() * 1 - 0.5)/3, (world.random.nextFloat() * 1 - 0.5)/3);
                            }
                            break;
                        }
                    }

                }
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
