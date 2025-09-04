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
    public static ResourceLocation TEXTURE = ArsNouveau.prefix("textures/entity/bubble.png");
    public static ResourceLocation POP_1 = ArsNouveau.prefix("textures/entity/bubble_pop1.png");
    public static ResourceLocation POP_2 = ArsNouveau.prefix("textures/entity/bubble_pop2.png");
    public static ResourceLocation POP_3 = ArsNouveau.prefix("textures/entity/bubble_pop3.png");
    public static ResourceLocation POP_4 = ArsNouveau.prefix("textures/entity/bubble_pop4.png");
    public static ResourceLocation POP_5 = ArsNouveau.prefix("textures/entity/bubble_pop5.png");

    public BubbleRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager);
    }

    @Override
    public ResourceLocation getTextureLocation(BubbleEntity pEntity) {
        return TEXTURE;
    }

    @Override
    public void render(BubbleEntity entityIn, float pEntityYaw, float pPartialTick, PoseStack matrixStack, MultiBufferSource buffer, int pPackedLight) {
        renderBubble(entityIn, this.entityRenderDispatcher, matrixStack, buffer);
    }

    public static void renderBubble(Entity entityIn, EntityRenderDispatcher entityRenderDispatcher, PoseStack matrixStack, MultiBufferSource buffer) {
        double y = entityIn.getPassengers().isEmpty() ? 0.25f : entityIn.getBbHeight();
        matrixStack.pushPose();
        matrixStack.translate(0, y, 0);
        matrixStack.mulPose(entityRenderDispatcher.cameraOrientation());

        matrixStack.scale(0.025F, -0.025F, 0.025F);
        float base = 2.0f;
        var passenger = entityIn.getFirstPassenger();
        if (passenger != null) {
            base += 1f;
            // Compare the size difference between the bubble hitbox and the passenger
            base *= passenger.getBbWidth() / entityIn.getBbWidth();
        }
        matrixStack.scale(base, base, base);
        final Matrix4f pose = matrixStack.last().pose();
        ResourceLocation texture = TEXTURE;

        if (entityIn instanceof BubbleEntity bubbleEntity && bubbleEntity.poppingTicks > 0) {
            int popTicks = bubbleEntity.poppingTicks;
            if (popTicks < 2) {
                texture = POP_1;
            } else if (popTicks < 3) {
                texture = POP_2;
            } else if (popTicks < 4) {
                texture = POP_3;
            } else if (popTicks < 5) {
                texture = POP_4;
            } else {
                texture = POP_5;
            }
        }
        VertexConsumer r = buffer.getBuffer(ShaderRegistry.worldEntityIcon(texture));
        r.addVertex(pose, -8, -8, 0).setUv(0, 0);
        r.addVertex(pose, -8, 8, 0).setUv(0, 1);
        r.addVertex(pose, 8, 8, 0).setUv(1, 1);
        r.addVertex(pose, 8, -8, 0).setUv(1, 0);

        matrixStack.popPose();
    }
}
