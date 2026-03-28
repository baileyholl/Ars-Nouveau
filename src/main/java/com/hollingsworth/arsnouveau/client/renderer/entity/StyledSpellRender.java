package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.client.renderer.ANDataTickets;
import com.hollingsworth.arsnouveau.common.entity.EntityProjectileSpell;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.RenderPassInfo;

/**
 * GeckoLib 5 renderer for EntityProjectileSpell.
 *
 * extractRenderState:
 *   - super call triggers GeoEntityRenderer → model.addAdditionalStateData → MODEL_TICKET/TEXTURE_TICKET
 *   - also stores interpolated yRot/xRot in PROJ_Y_ROT/PROJ_X_ROT for trajectory orientation
 *
 * adjustRenderPose:
 *   - reads rotation tickets and rotates the model so it faces along its flight path
 */
public class StyledSpellRender extends GeoEntityRenderer<EntityProjectileSpell, ArsEntityRenderState> {

    public StyledSpellRender(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new StyledProjectileModel());
    }

    @Override
    public ArsEntityRenderState createRenderState(EntityProjectileSpell animatable, Void context) {
        return new ArsEntityRenderState();
    }

    @Override
    public void extractRenderState(EntityProjectileSpell entity, ArsEntityRenderState renderState, float partialTick) {
        super.extractRenderState(entity, renderState, partialTick); // triggers addAdditionalStateData
        // Interpolated trajectory rotation for adjustRenderPose
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
        if (yRot != null) renderPassInfo.poseStack().mulPose(Axis.YP.rotationDegrees(yRot - 90f));
        if (xRot != null) renderPassInfo.poseStack().mulPose(Axis.ZP.rotationDegrees(xRot));
    }

    @Override
    public RenderType getRenderType(ArsEntityRenderState renderState, Identifier texture) {
        return RenderTypes.entityCutout(texture);
    }
}
