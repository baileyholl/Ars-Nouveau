package com.hollingsworth.arsnouveau.client.particle;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.client.gui.Color;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public class RainbowParticleColor extends ParticleColor {

    public static final ResourceLocation ID = ArsNouveau.prefix("rainbow");

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
        Color color = Color.rainbowColor(ticks);
        RainbowParticleColor color1 = new RainbowParticleColor(color.getRed(), color.getGreen(), color.getBlue());
        color1.tickOffset = this.tickOffset;
        return color1;
    }

    @Override
    public float getRed() {
        return Color.rainbowColor(tickOffset + ClientInfo.ticksInGame).getRedAsFloat();
    }

    @Override
    public int getRedInt() {
        return Color.rainbowColor(tickOffset + ClientInfo.ticksInGame).getRed();
    }

    @Override
    public float getBlue() {
        return Color.rainbowColor(tickOffset + ClientInfo.ticksInGame).getBlueAsFloat();
    }

    @Override
    public int getBlueInt() {
        return Color.rainbowColor(tickOffset + ClientInfo.ticksInGame).getBlue();
    }

    @Override
    public int getGreenInt() {
        return Color.rainbowColor(tickOffset + ClientInfo.ticksInGame).getGreen();
    }

    @Override
    public float getGreen() {
        return Color.rainbowColor(tickOffset + ClientInfo.ticksInGame).getGreenAsFloat();
    }

    @Override
    public int getColor() {
        return fromInt(Color.rainbowColor(tickOffset + ClientInfo.ticksInGame).getRGB()).getColor();
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
