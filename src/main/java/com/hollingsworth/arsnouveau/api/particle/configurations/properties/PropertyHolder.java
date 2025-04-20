package com.hollingsworth.arsnouveau.api.particle.configurations.properties;

import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import net.minecraft.core.particles.ParticleType;

import java.util.function.Consumer;

public class PropertyHolder {
    public Consumer<ParticleType<?>> onTextureChanged;
    public ParticleType<?> defaultType;
    public Consumer<ParticleColor> colorChanged;
    public ParticleColor defaultColor;

    public PropertyHolder(
            Consumer<ParticleType<?>> onTextureChanged,
            ParticleType<?> defaultType,
            Consumer<ParticleColor> colorChanged,
            ParticleColor defaultColor
    ) {
        this.onTextureChanged = onTextureChanged;
        this.defaultType = defaultType;
        this.colorChanged = colorChanged;
        this.defaultColor = defaultColor;
    }
}
