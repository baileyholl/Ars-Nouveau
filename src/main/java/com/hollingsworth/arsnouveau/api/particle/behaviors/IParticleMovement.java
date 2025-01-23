package com.hollingsworth.arsnouveau.api.particle.behaviors;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.particle.Particle;

/**
 * Callback behavior for every particle
 */
public interface IParticleMovement {

    void init(Particle particle);

    void tick();

    void render(VertexConsumer buffer, Camera renderInfo, float partialTicks);
}
