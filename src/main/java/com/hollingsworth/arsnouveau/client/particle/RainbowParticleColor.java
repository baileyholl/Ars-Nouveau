package com.hollingsworth.arsnouveau.client.particle;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.client.gui.Color;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public class RainbowParticleColor extends ParticleColor {

    public static final ResourceLocation ID = ArsNouveau.prefix("rainbow");

    private static final ParticleColor[] RAINBOW_FRAMES = new ParticleColor[1536];

    static {
        for (int i = 0; i < RAINBOW_FRAMES.length; i++) {
            RAINBOW_FRAMES[i] = Color.rainbowColor(i).toParticle();
        }
    }

    public int tickOffset;

    public RainbowParticleColor(int r, int g, int b) {
        super(r, g, b);
        this.tickOffset = random.nextInt(1536);
    }

    public RainbowParticleColor(CompoundTag compoundTag) {
        super(compoundTag);
        tickOffset = compoundTag.getInt("tickOffset");
    }

    @Override
    public ParticleColor nextColor(int ticks) {
        return Color.rainbowColor(ticks).toParticle();
    }

    @Override
    public ParticleColor transition(int ticks) {
        return RAINBOW_FRAMES[Math.abs((tickOffset + ticks) % RAINBOW_FRAMES.length)];
    }

    @Override
    public CompoundTag serialize() {
        CompoundTag tag = super.serialize();
        tag.putInt("tickOffset", tickOffset);
        return tag;
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
    public int hashCode() {
        return Objects.hash(getRegistryName());
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (o instanceof RainbowParticleColor color &&
                this.getRegistryName().equals(color.getRegistryName()));
    }
}
