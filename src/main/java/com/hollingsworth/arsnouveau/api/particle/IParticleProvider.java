package com.hollingsworth.arsnouveau.api.particle;

import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import net.minecraft.nbt.CompoundTag;

public interface IParticleProvider {
    ParticleColor create(CompoundTag tag);

    ParticleColor create(int r, int g, int b);
}
