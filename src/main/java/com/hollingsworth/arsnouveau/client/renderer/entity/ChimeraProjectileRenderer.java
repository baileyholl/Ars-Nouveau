package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.client.renderer.ANDataTickets;
import com.hollingsworth.arsnouveau.common.entity.EntityChimeraProjectile;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.RenderPassInfo;

/**
 * GeckoLib 5 renderer for EntityChimeraProjectile (Wilden Chimera boss spike).
 * Stores interpolated yRot/xRot in render state and applies them in adjustRenderPose.
 */
public class ChimeraProjectileRenderer extends GeoEntityRenderer<EntityChimeraProjectile, ArsEntityRenderState> {

    public ChimeraProjectileRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ChimeraProjectileModel());
    }

    @Override
    public ArsEntityRenderState createRenderState(EntityChimeraProjectile animatable, Void context) {
        return new ArsEntityRenderState();
    }

    @Override
    public void extractRenderState(EntityChimeraProjectile entity, ArsEntityRenderState renderState, float partialTick) {
        super.extractRenderState(entity, renderState, partialTick);
        float yRot = Mth.rotLerp(partialTick, entity.yRotO, entity.getYRot());
        float xRot = Mth.lerp(partialTick, entity.xRotO, entity.getXRot());
        renderState.addGeckolibData(ANDataTickets.PROJ_Y_ROT, yRot);
        renderState.addGeckolibData(ANDataTickets.PROJ_X_ROT, xRot);
    }

    @Override
    public void adjustRenderPose(RenderPassInfo<ArsEntityRenderState> renderPassInfo) {
        super.adjustRenderPose(renderPassInfo);
        Float yRot = renderPassInfo.renderState().getGeckolibData(ANDataTickets.PROJ_Y_ROT);
        Float xRot = renderPassInfo.renderState().getGeckolibData(ANDataTickets.PROJ_X_ROT);
        if (yRot != null) renderPassInfo.poseStack().mulPose(Axis.YP.rotationDegrees(yRot - 90.0f));
        if (xRot != null) renderPassInfo.poseStack().mulPose(Axis.ZP.rotationDegrees(xRot));
    }
}
