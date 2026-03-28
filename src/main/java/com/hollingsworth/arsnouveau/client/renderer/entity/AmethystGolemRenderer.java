package com.hollingsworth.arsnouveau.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

// GeckoLib 5.4.2 migration:
// - GeoEntityRenderer requires R extends EntityRenderState & GeoRenderState
// - LivingEntityRenderState does NOT extend GeoRenderState, so we use ArsEntityRenderState
// - preRender() and renderRecursively() REMOVED
// - Per-bone item rendering (item bone) needs to be ported to RenderPassInfo.addPerBoneRender
// TODO: Port item-in-hand rendering for the "item" bone
public class AmethystGolemRenderer<T extends LivingEntity & GeoEntity> extends GeoEntityRenderer<T, ArsEntityRenderState> {
    public AmethystGolemRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new AmethystGolemModel<>());
    }

    // GeckoLib 5: createRenderState(T, Void) is the correct override (no-arg is final)
    @Override
    public ArsEntityRenderState createRenderState(T animatable, Void context) {
        return new ArsEntityRenderState();
    }

    // GeckoLib 5: getRenderType(R renderState, Identifier texture) - new signature
    @Override
    public RenderType getRenderType(ArsEntityRenderState renderState, Identifier texture) {
        return RenderTypes.entityCutoutNoCull(texture);
    }

    // TODO: GeckoLib 5 - renderRecursively and preRender removed.
    // Previously: on "item" bone, rendered the golem's mainhand item using stored bufferSource.
    // Needs to be migrated to captureDefaultRenderState + addPerBoneRender pattern.
}
