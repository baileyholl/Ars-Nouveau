package com.hollingsworth.arsnouveau.api.particle;

import com.hollingsworth.arsnouveau.api.nbt.ITagSerializable;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import net.minecraft.resources.ResourceLocation;

public interface IParticleColor extends ITagSerializable, Cloneable {

    float getRed();

    float getGreen();

    float getBlue();

    int getColor();

    default ParticleColor transition(int ticks){
        return transition();
    }

    default ParticleColor transition(){
        return (ParticleColor) this;
    }

    default ParticleColor transitionTowards(ParticleColor color) {
        return (ParticleColor) this;
    }

    default int getRedInt(){
        return (int) (getRed() * 255.0F);
    }

    default int getGreenInt(){
        return (int) (getGreen() * 255.0F);
    }

    default int getBlueInt(){
        return (int) (getBlue() * 255.0F);
    }

    ResourceLocation getRegistryName();
}
