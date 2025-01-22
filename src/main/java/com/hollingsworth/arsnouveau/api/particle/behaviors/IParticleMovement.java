package com.hollingsworth.arsnouveau.api.particle.behaviors;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.particle.Particle;

public interface IParticleMovement {

    void init(Particle particle);

    void tick(Particle particle);

    void render(VertexConsumer buffer, Camera renderInfo, float partialTicks);
}
