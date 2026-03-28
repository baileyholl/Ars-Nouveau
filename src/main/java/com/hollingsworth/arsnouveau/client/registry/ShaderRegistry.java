package com.hollingsworth.arsnouveau.client.registry;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Custom render types for AN shaders.
 * TODO: Full RenderPipeline migration — MC 1.21.11 replaced ShaderInstance+RenderStateShard with
 *   RenderPipeline+RegisterRenderPipelinesEvent+RenderSetup. Custom shaders (sky, rainbow_entity,
 *   blamed_entity) need to be ported to RenderPipeline.builder(...).withVertexShader(...).build()
 *   and registered via RegisterRenderPipelinesEvent. Until then these stub to entityCutoutNoCull.
 */
public class ShaderRegistry {

    // TODO: Sky block render type — needs RenderPipeline with sky.vsh/fsh + TextureTarget input
    public static final RenderType SKY_RENDER_TYPE = RenderTypes.entityCutoutNoCull(
            ArsNouveau.prefix("textures/block/sky_weave.png")
    );

    // TODO: Rainbow entity — needs RenderPipeline with rainbow_entity.vsh/fsh + two texture samplers
    private static final BiFunction<Identifier, Boolean, RenderType> RAINBOW_ENTITY_RENDER_TYPE =
            Util.memoize((location, outline) -> RenderTypes.entityCutoutNoCull(location, outline));

    // TODO: Blame entity — needs RenderPipeline with blamed_entity.vsh/fsh (animated noise warp)
    private static final BiFunction<Identifier, Boolean, RenderType> BLAMED_TYPE =
            Util.memoize((location, outline) -> RenderTypes.entityCutoutNoCull(location, outline));

    public static RenderType rainbowEntity(Identifier location, Identifier mask, boolean outline) {
        return RAINBOW_ENTITY_RENDER_TYPE.apply(location, outline);
    }

    public static RenderType blamed(Identifier location, boolean outline) {
        return BLAMED_TYPE.apply(location, outline);
    }

    // TODO: World entity icon — needs translucent position-tex render type via RenderPipeline
    // Cast to Function<Identifier, RenderType> to disambiguate memoize overload (entityTranslucent has 1-arg and 2-arg)
    private static final Function<Identifier, RenderType> WORLD_ENTITY_ICON =
            Util.memoize((Function<Identifier, RenderType>) RenderTypes::entityTranslucent);

    public static RenderType worldEntityIcon(final Identifier resLoc) {
        return WORLD_ENTITY_ICON.apply(resLoc);
    }
}
