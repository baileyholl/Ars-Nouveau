package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.client.CosmeticRenderUtil;
import com.hollingsworth.arsnouveau.api.item.ICosmeticItem;
import com.hollingsworth.arsnouveau.client.registry.ShaderRegistry;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

// GeckoLib 5.4.2 migration:
// - GeoEntityRenderer now requires 2 type params: <T, R extends EntityRenderState & GeoRenderState>
//   Using ArsEntityRenderState to satisfy both bounds at compile time.
// - renderRecursively() REMOVED - per-bone item/cosmetic rendering needs to use RenderPassInfo.addPerBoneRender
// - getRenderType signature changed to (R renderState, Identifier texture)
// - getTextureLocation(T) no longer in GeoEntityRenderer — override removed
// TODO: Port renderRecursively logic (item-in-hand, cosmetic rendering) to addRenderData/preRenderPass
// TODO: Store entity name in render state (via DataTickets) so special shader lookup can use it
public class StarbuncleRenderer extends GeoEntityRenderer<Starbuncle, ArsEntityRenderState> {
    public static Map<String, Function<Identifier, RenderType>> specialShaders = new HashMap<>();

    static {
        // Jared's special shader, because adopter details aren't synced.
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

    // TODO: GeckoLib 5 - renderRecursively removed. Port item-in-hand and cosmetic rendering.
    // Previously rendered an item on the "item" bone and a cosmetic on the cosmetic bone.
    // In GeckoLib 5, use addRenderData() to capture entity data and addPerBoneRender() in preRenderPass.
    // Starbuncle.getHeldStack(), dynamicBehavior.getStackForRender(), and getCosmeticItem() need to be
    // stored in render state via captureDefaultRenderState then used in bone render tasks.

    // GeckoLib 5: getTextureLocation(T) no longer overridable; texture is model-driven via getTextureResource()
    // The entity-specific texture (entity.getTexture()) is returned from StarbuncleModel.getTextureResource()
    // TODO: Move entity.getTexture() logic into StarbuncleModel using addAdditionalStateData + DataTickets

    // GeckoLib 5: getRenderType(R renderState, Identifier texture) - new signature
    @Override
    public RenderType getRenderType(ArsEntityRenderState renderState, Identifier textureLocation) {
        // TODO: Store entity name in render state via captureDefaultRenderState and DataTickets
        // so we can do proper special shader lookup by entity name
        return specialShaders.getOrDefault("", RenderTypes::entityCutoutNoCull).apply(textureLocation);
    }
}
