package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.common.entity.EntityDummy;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;

// MC 1.21.11 / LivingEntityRenderer is now 3 type params <T, S extends LivingEntityRenderState, M extends EntityModel<? super S>>
// PlayerModel<EntityDummy> is no longer valid: PlayerModel now takes AvatarRenderState, not mob entities.
// DummyRenderer is ported to HumanoidMobRenderer with HumanoidModel and HumanoidRenderState.
//
// TODO: Port player-skin texture (getSkinTextureLocation) via extractRenderState storing the skin Identifier.
// TODO: Port cape rendering (DummyCapeLayer) - requires AvatarRenderState or custom render state.
// TODO: Port hand rendering (renderRightHand / renderLeftHand) - no direct equivalent in new API.
// TODO: Port crouching/arm-pose logic - now driven by HumanoidRenderState fields set by extractRenderState.
public class DummyRenderer extends HumanoidMobRenderer<EntityDummy, HumanoidRenderState, HumanoidModel<HumanoidRenderState>> {

    private static final Identifier DEFAULT_TEXTURE = Identifier.withDefaultNamespace("textures/entity/player/wide/steve.png");

    public DummyRenderer(EntityRendererProvider.Context context) {
        super(context,
                new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER)),
                0.5F);
    }

    @Override
    public HumanoidRenderState createRenderState() {
        return new HumanoidRenderState();
    }

    @Override
    public void extractRenderState(EntityDummy entity, HumanoidRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        // TODO: Store player skin identifier from entity.getSkinTextureLocation() in a custom render state
        // so that getTextureLocation(S) can return it.
    }

    @Override
    public Identifier getTextureLocation(HumanoidRenderState renderState) {
        // TODO: Store actual player skin from entity in custom render state and return it here.
        return DEFAULT_TEXTURE;
    }

    @Override
    public Vec3 getRenderOffset(HumanoidRenderState state) {
        return state.isCrouching ? new Vec3(0.0, -0.125, 0.0) : super.getRenderOffset(state);
    }

    @Override
    protected boolean shouldShowName(EntityDummy entity, double distanceSq) {
        // Only show name tag if the entity explicitly wants to show it.
        return entity.shouldShowName() && super.shouldShowName(entity, distanceSq);
    }

    @Override
    protected void setupRotations(HumanoidRenderState state, PoseStack poseStack, float bodyYaw, float scale) {
        super.setupRotations(state, poseStack, bodyYaw, scale);
        // TODO: Port fall-flying and swimming rotation logic from old setupRotations(EntityDummy, ...)
        // Old code used entity.isFallFlying(), getFallFlyingTicks(), getSwimAmount(), etc.
        // These now come from HumanoidRenderState fields (isFallFlying, isVisuallySwimming, swimAmount).
    }
}
