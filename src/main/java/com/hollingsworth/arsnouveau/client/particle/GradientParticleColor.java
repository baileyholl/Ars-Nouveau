package com.hollingsworth.arsnouveau.client.particle;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.particle.IParticleColorType;
import com.hollingsworth.arsnouveau.api.registry.ParticleColorRegistry;
import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.Objects;

// TODO: breaking change: remove extension of particle color
public class GradientParticleColor extends ParticleColor {

    public static final ResourceLocation ID = ArsNouveau.prefix("gradient");

    public static final MapCodec<GradientParticleColor> GRADIENT_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ParticleColor.CODEC.fieldOf("color1").forGetter(GradientParticleColor::color1),
            ParticleColor.CODEC.fieldOf("color2").forGetter(GradientParticleColor::color2),
            Codec.INT.fieldOf("transitionTime").forGetter(GradientParticleColor::transitionTime)
    ).apply(instance, GradientParticleColor::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, GradientParticleColor> GRADIENT_STREAM = StreamCodec.composite(
            ParticleColor.STREAM, GradientParticleColor::color1,
            ParticleColor.STREAM, GradientParticleColor::color2,
            ByteBufCodecs.INT, GradientParticleColor::transitionTime,
            GradientParticleColor::new
    );
    private ParticleColor[] frames = null;

    public ParticleColor color1;
    public ParticleColor color2;
    public int transitionTime;
    public int tickOffset;

    public GradientParticleColor(ParticleColor color1, ParticleColor color2, int transitionTime) {
        super(0, 0, 0);
        this.color1 = color1;
        this.color2 = color2;
        this.transitionTime = transitionTime;
        this.tickOffset = random.nextInt(1536);
    }

    public void generateFrames() {
        int half = Math.max(transitionTime, 1);
        frames = new ParticleColor[half * 2];
        for (int ticks = 0; ticks < frames.length; ticks++) {
            float delta = 1 - Math.abs(ticks - half) / (float) half;

            float red = Mth.lerp(delta, color1.getRedInt(), color2.getRedInt());
            float green = Mth.lerp(delta, color1.getGreenInt(), color2.getGreenInt());
            float blue = Mth.lerp(delta, color1.getBlueInt(), color2.getBlueInt());
            frames[ticks] = new ParticleColor(red, green, blue);
        }
    }

    public ParticleColor color1() {
        return color1;
    }

    public ParticleColor color2() {
        return color2;
    }

    public int transitionTime() {
        return transitionTime;
    }


    @Deprecated(forRemoval = true) // Goes away when ParticleColor is no longer parent class
    public GradientParticleColor(int r, int g, int b) {
        super(r, g, b);
        this.tickOffset = random.nextInt(1536);
    }

    @Override
    public IParticleColorType<?> getType() {
        return ParticleColorRegistry.GRADIENT.get();
    }

    @Override
    public ParticleColor nextColor(int ticks) {
        return transition(ticks);
    }

    @Override
    public ParticleColor transition(int ticks) {
        if (frames == null) {
            generateFrames();
        }
        return frames[Math.abs((tickOffset + ticks) % frames.length)];
    }

    @Override
    public float getRed() {
        return transition(ClientInfo.ticksInGame).getRed();
    }

    @Override
    public int getRedInt() {
        return transition(ClientInfo.ticksInGame).getRedInt();
    }

    @Override
    public float getBlue() {
        return transition(ClientInfo.ticksInGame).getBlue();
    }

    @Override
    public int getBlueInt() {
        return transition(ClientInfo.ticksInGame).getBlueInt();
    }

    @Override
    public int getGreenInt() {
        return transition(ClientInfo.ticksInGame).getGreenInt();
    }

    @Override
    public float getGreen() {
        return transition(ClientInfo.ticksInGame).getGreen();
    }

    @Override
    public int getColor() {
        return transition(ClientInfo.ticksInGame).getColor();
    }

    @Override
    public ResourceLocation getRegistryName() {
        return ID;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        GradientParticleColor that = (GradientParticleColor) o;
        return transitionTime == that.transitionTime && Objects.equals(color1, that.color1) && Objects.equals(color2, that.color2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), color1, color2, transitionTime);
    }
}
