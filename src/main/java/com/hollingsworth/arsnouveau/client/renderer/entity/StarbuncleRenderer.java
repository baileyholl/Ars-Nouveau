package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.registry.ShaderRegistry;
import com.hollingsworth.arsnouveau.client.renderer.ANDataTickets;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

// GeckoLib 5.4.2 migration:
// - GeoEntityRenderer now requires 2 type params: <T, R extends EntityRenderState & GeoRenderState>
// - renderRecursively() REMOVED - per-bone item/cosmetic rendering stubbed (see addRenderData TODOs)
// - getRenderType signature changed to (R renderState, Identifier texture)
// - Entity name captured via addRenderData + ANDataTickets.ENTITY_NAME for special shader lookup
// TODO: Port renderRecursively logic (item-in-hand, cosmetic) to addPerBoneRender in preRenderPass
public class StarbuncleRenderer extends GeoEntityRenderer<Starbuncle, ArsEntityRenderState> {
    public static Map<String, Function<Identifier, RenderType>> specialShaders = new HashMap<>();

    static {
        // Special shaders keyed by entity display name (synced via custom name on entity)
        specialShaders.put("Splonk", (texture) -> ShaderRegistry.blamed(texture, true));
        specialShaders.put("Bailey", (texture) -> ShaderRegistry.rainbowEntity(texture, ArsNouveau.prefix("textures/entity/starbuncle_mask.png"), true));
        specialShaders.put("Gootastic", RenderTypes::entityTranslucent);
    }

    public StarbuncleRenderer(EntityRendererProvider.Context manager) {
        super(manager, new StarbuncleModel());
    }

    // GeckoLib 5: createRenderState(T, Void) is the correct override
    @Override
    public ArsEntityRenderState createRenderState(Starbuncle animatable, Void context) {
        return new ArsEntityRenderState();
    }

    @Override
    public void addRenderData(Starbuncle animatable, @Nullable Void relatedObject, ArsEntityRenderState renderState, float partialTick) {
        renderState.addGeckolibData(ANDataTickets.ENTITY_NAME, animatable.getName().getString());
    }

    // GeckoLib 5: getRenderType(R renderState, Identifier texture) - new signature
    @Override
    public RenderType getRenderType(ArsEntityRenderState renderState, Identifier textureLocation) {
        String name = renderState.getGeckolibData(ANDataTickets.ENTITY_NAME);
        if (name != null && specialShaders.containsKey(name)) {
            return specialShaders.get(name).apply(textureLocation);
        }
        return RenderTypes.entityCutoutNoCull(textureLocation);
    }
}
