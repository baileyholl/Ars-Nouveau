package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.common.entity.EntityBookwyrm;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

// GeckoLib 5.4.2 migration:
// - GeoEntityRenderer requires R extends EntityRenderState & GeoRenderState
// - LivingEntityRenderState does NOT extend GeoRenderState, so we use ArsEntityRenderState
// - renderRecursively() REMOVED - "item" bone rendering needs to be ported
// - getTextureLocation(T) removed; texture comes from model's getTextureResource(GeoRenderState)
// TODO: Port bookwyrm texture (dynamic per-entity) to DataTicket in captureDefaultRenderState
// TODO: Port 0.6f scale from old render() override to scaleModelForRender(RenderPassInfo, float, float)
public class BookwyrmRenderer extends GeoEntityRenderer<EntityBookwyrm, ArsEntityRenderState> {

    public BookwyrmRenderer(EntityRendererProvider.Context manager) {
        super(manager, new BookwyrmModel<>());
    }

    // GeckoLib 5: createRenderState(T, Void) is the correct override (no-arg is final)
    @Override
    public ArsEntityRenderState createRenderState(EntityBookwyrm animatable, Void context) {
        return new ArsEntityRenderState();
    }

    // TODO: Port 0.6f scale from old render() override to scaleModelForRender(RenderPassInfo, float, float)
    @Override
    public void scaleModelForRender(software.bernie.geckolib.renderer.base.RenderPassInfo<ArsEntityRenderState> renderPassInfo, float width, float height) {
        renderPassInfo.poseStack().scale(0.6f, 0.6f, 0.6f);
        super.scaleModelForRender(renderPassInfo, width, height);
    }
}
