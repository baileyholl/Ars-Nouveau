package com.hollingsworth.arsnouveau.client.particle;

import com.mojang.serialization.Codec;
import net.minecraft.core.particles.ParticleType;

public class HelixParticleType extends ParticleType<HelixParticleTypeData> {
    public HelixParticleType() {
        super(false, HelixParticleTypeData.DESERIALIZER);
    }

    @Override
    public Codec<HelixParticleTypeData> codec() {
        return HelixParticleTypeData.CODEC;
    }

}
