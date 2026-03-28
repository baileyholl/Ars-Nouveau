package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.ArchwoodBoat;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.AbstractBoatRenderer;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.BoatRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

// MC 1.21.11: BoatRenderer.getModelWithLocation() removed.
// In 1.21.11, BoatRenderer takes a ModelLayerLocation and uses model() + renderType() for customization.
// For custom boat textures, we extend BoatRenderer and override renderType() to use our custom texture.
//
// TODO: The boat variant (archwood type) is synced via entity data, but BoatRenderState doesn't carry it.
// To use variant-specific textures, we need to:
// 1. Create a custom BoatRenderState subclass with an Identifier field for the texture.
// 2. Override extractRenderState() to populate the custom texture from ArchwoodBoat entity data.
// 3. Override renderType() to use the custom texture from the render state.
// For now, a default archwood texture is used for all archwood boats.
@OnlyIn(Dist.CLIENT)
public class ArchwoodBoatRenderer extends BoatRenderer {

    private static final Identifier DEFAULT_TEXTURE = ArsNouveau.prefix("textures/entity/boat/archwood.png");

    public ArchwoodBoatRenderer(EntityRendererProvider.Context renderContext, boolean isChestBoat) {
        super(renderContext, isChestBoat ? ModelLayers.ACACIA_CHEST_BOAT : ModelLayers.ACACIA_BOAT);
    }

    @Override
    protected @NotNull RenderType renderType() {
        // TODO: Return variant-specific texture based on render state boat variant.
        // Requires custom BoatRenderState with texture captured from ArchwoodBoat.getArchwoodVariant().
        return RenderTypes.entityCutoutNoCull(DEFAULT_TEXTURE);
    }
}
