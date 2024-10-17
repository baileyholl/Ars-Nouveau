package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.registry.ShaderRegistry;
import com.hollingsworth.arsnouveau.common.entity.BubbleEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.joml.Matrix4f;

public class BubbleRenderer extends EntityRenderer<BubbleEntity> {

    public BubbleRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager);
    }

    @Override
    public ResourceLocation getTextureLocation(BubbleEntity pEntity) {
        return ArsNouveau.prefix("textures/entity/bubble.png");
    }


    @Override
    public void render(BubbleEntity entityIn, float pEntityYaw, float pPartialTick, PoseStack matrixStack, MultiBufferSource buffer, int pPackedLight) {
        renderBubble(entityIn, this.entityRenderDispatcher, matrixStack, buffer);
    }

    public static void renderBubble(Entity entityIn, EntityRenderDispatcher entityRenderDispatcher, PoseStack matrixStack, MultiBufferSource buffer){
        double y = entityIn.getBbHeight();
        matrixStack.pushPose();
        matrixStack.translate(0, y, 0);
        matrixStack.mulPose(entityRenderDispatcher.cameraOrientation());

        matrixStack.scale(0.025F, -0.025F, 0.025F);
        float base = 2.0f;
        var passenger = entityIn.getFirstPassenger();
        if(passenger != null){
            base += 1f;
            // Compare the size difference between the bubble hitbox and the passenger
            base *= passenger.getBbWidth() / entityIn.getBbWidth();
        }
        matrixStack.scale(base, base, base);
        final Matrix4f pose = matrixStack.last().pose();

        VertexConsumer r = buffer.getBuffer(ShaderRegistry.worldEntityIcon(ArsNouveau.prefix("textures/entity/bubble.png")));
        r.addVertex(pose, -8, -8, 0).setUv(0, 0);
        r.addVertex(pose, -8, 8, 0).setUv(0, 1);
        r.addVertex(pose, 8, 8, 0).setUv(1, 1);
        r.addVertex(pose, 8, -8, 0).setUv(1, 0);

        matrixStack.popPose();
    }
}
