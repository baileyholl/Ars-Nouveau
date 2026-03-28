package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.EntityFlyingItem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

// MC 1.21.11: EntityRenderer uses submit() + render states; item rendering via ItemModelResolver
public class RenderFlyingItem extends EntityRenderer<EntityFlyingItem, RenderFlyingItem.FlyingItemRenderState> {

    private final ItemModelResolver itemModelResolver;

    public static class FlyingItemRenderState extends EntityRenderState {
        public boolean isBubble;
        public final ItemStackRenderState item = new ItemStackRenderState();
        public int blockPosHash;
    }

    public RenderFlyingItem(EntityRendererProvider.Context renderManager) {
        super(renderManager);
        this.itemModelResolver = renderManager.getItemModelResolver();
    }

    @Override
    public FlyingItemRenderState createRenderState() {
        return new FlyingItemRenderState();
    }

    @Override
    public void extractRenderState(EntityFlyingItem entity, FlyingItemRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.isBubble = entity.getEntityData().get(EntityFlyingItem.IS_BUBBLE);
        ItemStack stack = entity.getStack() != null ? entity.getStack() : ItemStack.EMPTY;
        this.itemModelResolver.updateForNonLiving(state.item, stack, ItemDisplayContext.GROUND, entity);
        state.blockPosHash = (int) entity.blockPosition().asLong();
    }

    @Override
    public void submit(FlyingItemRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
        super.submit(state, poseStack, collector, cameraState);

        if (state.isBubble) {
            poseStack.pushPose();
            BubbleRenderer.renderBubble(null, this.entityRenderDispatcher, poseStack,
                    Minecraft.getInstance().renderBuffers().bufferSource());
            poseStack.popPose();
        }

        poseStack.pushPose();
        if (state.isBubble) {
            poseStack.translate(0, 0.5, 0);
        }
        poseStack.scale(0.35f, 0.35f, 0.35f);
        state.item.submit(poseStack, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);
        poseStack.popPose();
    }

    // 1.21.11: EntityRenderer no longer has getTextureLocation(T entity) — not an @Override
    public Identifier getTextureLocation(EntityFlyingItem entity) {
        return ArsNouveau.prefix("textures/entity/spell_proj.png");
    }
}
