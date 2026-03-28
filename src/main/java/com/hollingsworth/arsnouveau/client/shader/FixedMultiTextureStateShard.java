package com.hollingsworth.arsnouveau.client.shader;

import java.util.List;

/**
 * Previously extended RenderStateShard.EmptyTextureStateShard to bind multiple textures for
 * the rainbow entity shader. RenderStateShard is gone in MC 1.21.11 — multi-texture binding
 * is now handled via RenderSetup.builder(pipeline).withTexture(...) in the RenderPipeline API.
 * TODO: Remove this class entirely once ShaderRegistry is ported to RenderPipeline.
 */
public class FixedMultiTextureStateShard {

    public FixedMultiTextureStateShard(List<Texture> textures) {
        // no-op stub — previously set GL texture state; now handled by RenderPipeline
    }
}
