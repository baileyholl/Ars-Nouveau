package com.hollingsworth.arsnouveau.client.particle;

import net.minecraft.client.particle.SingleQuadParticle;

// In 1.21.11, custom ParticleRenderType implementations are gone.
// Particles now use SingleQuadParticle.Layer for blending control.
public class ParticleRenderTypes {
    public static final SingleQuadParticle.Layer EMBER_RENDER = SingleQuadParticle.Layer.TRANSLUCENT;
    public static final SingleQuadParticle.Layer EMBER_RENDER_NO_MASK = SingleQuadParticle.Layer.TRANSLUCENT;
    public static final SingleQuadParticle.Layer ADDITIVE = SingleQuadParticle.Layer.TRANSLUCENT;
}
