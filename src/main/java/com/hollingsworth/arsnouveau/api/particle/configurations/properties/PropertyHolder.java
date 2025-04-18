package com.hollingsworth.arsnouveau.api.particle.configurations.properties;

import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

import java.util.function.Consumer;

public class PropertyHolder {
    public Consumer<ParticleType<? extends ParticleOptions>> onTextureChanged;
    public ParticleType<? extends ParticleOptions> defaultType;
    public Consumer<ParticleColor> colorChanged;
    public ParticleColor defaultColor;



    public PropertyHolder(
            Consumer<ParticleType<? extends ParticleOptions>> onTextureChanged,
            ParticleType<? extends ParticleOptions> defaultType,
            Consumer<ParticleColor> colorChanged,
            ParticleColor defaultColor
    ) {
//        this.subPropsChanged = subPropsChanged;
        this.onTextureChanged = onTextureChanged;
        this.defaultType = defaultType;
        this.colorChanged = colorChanged;
        this.defaultColor = defaultColor;
    }
}
