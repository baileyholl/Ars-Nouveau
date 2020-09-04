package com.hollingsworth.arsnouveau.client.particle;

import com.mojang.serialization.Codec;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;

public class GlowParticleType extends ParticleType<ColorParticleTypeData> {
    public GlowParticleType() {
        super(false, ColorParticleTypeData.DESERIALIZER);
    }

    @Override
    public Codec<ColorParticleTypeData> func_230522_e_() {
        return ColorParticleTypeData.CODEC;
    }
}
