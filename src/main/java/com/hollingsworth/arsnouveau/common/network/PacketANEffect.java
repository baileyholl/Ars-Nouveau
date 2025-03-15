package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.registry.ParticleColorRegistry;
import com.hollingsworth.arsnouveau.client.particle.GlowParticleData;
import com.hollingsworth.arsnouveau.client.particle.HelixParticleData;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

public class PacketANEffect extends AbstractPacket{
    public static final Type<PacketANEffect> TYPE = new Type<>(ArsNouveau.prefix("effect"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketANEffect> CODEC = StreamCodec.ofMember(PacketANEffect::toBytes, PacketANEffect::decode);
    private final EffectType type;
    private final double x;
    private final double y;
    private final double z;
    private final CompoundTag particleNbt;

    private final int[] args;

    public PacketANEffect(EffectType type, double x, double y, double z, int... args) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.z = z;
        //255,25,180
        ParticleColor defaultColor = new ParticleColor(255, 25, 180);
        this.particleNbt = defaultColor.serialize();
        this.args = args;
    }

    public PacketANEffect(EffectType type, double x, double y, double z, ParticleColor color, int... args) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.z = z;
        this.particleNbt = color.serialize();
        this.args = args;
    }


    public PacketANEffect(EffectType type, BlockPos pos, int... args) {
        this(type, pos.getX(), pos.getY(), pos.getZ(), args);
    }

    public PacketANEffect(EffectType type, BlockPos pos, ParticleColor wrapper, int... args) {
        this(type, pos.getX(), pos.getY(), pos.getZ(), wrapper, args);
    }

    public static PacketANEffect decode(RegistryFriendlyByteBuf buf) {
        EffectType type = EffectType.values()[buf.readByte()];
        double x = buf.readDouble();
        double y = buf.readDouble();
        double z = buf.readDouble();
        CompoundTag nbt = buf.readNbt();
        int[] args = new int[type.argCount];

        for (int i = 0; i < args.length; i++) {
            args[i] = buf.readVarInt();
        }
        return new PacketANEffect(type, x, y, z, ParticleColorRegistry.from(nbt), args);
    }

    @Override
    public void toBytes(RegistryFriendlyByteBuf buf) {
        buf.writeByte(type.ordinal());
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
        buf.writeNbt(particleNbt);
        for (int i = 0; i < type.argCount; i++) {
            buf.writeVarInt(args[i]);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        ClientLevel world = minecraft.level;
        ParticleColor color = ParticleColorRegistry.from(particleNbt);
        switch (type) {
            case BURST -> {
                for (int i = 0; i < 10; i++) {
                    double d0 = x + 0.5; //+ world.rand.nextFloat();
                    double d1 = y + 1.2;//+ world.rand.nextFloat() ;
                    double d2 = z + .5; //+ world.rand.nextFloat();
                    world.addAlwaysVisibleParticle(GlowParticleData.createData(color),
                            true,
                            d0, d1, d2,
                            (world.random.nextFloat() - 0.5) / 3.0,
                            (world.random.nextFloat() - 0.5) / 3.0,
                            (world.random.nextFloat() - 0.5) / 3.0);
                }
            }
            case TIMED_HELIX -> {

                int numParticles = 8;
                for (int i = 0; i < numParticles; i++) {
                    world.addAlwaysVisibleParticle(HelixParticleData.createData(color, 0.25f, 1.00f, 50, i * 360F / numParticles), true, x + 0.5, y + 0.1 * i, z + 0.5, 0, 0, 0);
                }
            }
        }
    }
    public enum EffectType {
        TIMED_GLOW(4), //dest xyz num_particles
        TIMED_HELIX(0),
        BURST(0);

        private final int argCount;

        EffectType(int argCount) {
            this.argCount = argCount;
        }
    }
}
