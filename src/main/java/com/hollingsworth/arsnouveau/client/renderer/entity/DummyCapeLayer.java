package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.common.entity.EntityDummy;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;

// MC 1.21.11 migration:
// - PlayerModel<EntityDummy> is invalid: PlayerModel now takes AvatarRenderState, not mob entities.
// - DummyCapeLayer ported to use HumanoidModel<HumanoidRenderState> matching DummyRenderer's type params.
// - RenderLayer.render() is now submit(PoseStack, SubmitNodeCollector, int, S, float, float).
// - PlayerSkin removed from layer context - cape texture would need to come from render state.
//
// TODO: Port cape rendering logic. Cape texture, cloak position data (xCloakO etc.) are only available
// on Player, not EntityDummy/HumanoidRenderState. Cape rendering for the dummy entity is not currently functional.
public class DummyCapeLayer extends RenderLayer<HumanoidRenderState, HumanoidModel<HumanoidRenderState>> {

    public DummyCapeLayer(RenderLayerParent<HumanoidRenderState, HumanoidModel<HumanoidRenderState>> renderer) {
        super(renderer);
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector collector, int packedLight, HumanoidRenderState renderState, float limbSwing, float limbSwingAmount) {
        // TODO: Port cape rendering. The cape texture and cloak position data (xCloakO, yCloak, zCloak, etc.)
        // are not available in HumanoidRenderState. This requires a custom render state that captures
        // the owner Player's cape data during extractRenderState.
    }
}
