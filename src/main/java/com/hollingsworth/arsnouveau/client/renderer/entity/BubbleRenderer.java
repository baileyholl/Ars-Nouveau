package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.registry.ShaderRegistry;
import com.hollingsworth.arsnouveau.common.entity.BubbleEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import org.joml.Matrix4f;

// MC 1.21.11: EntityRenderer uses submit() + render states. cameraOrientation() removed from EntityRenderDispatcher.
// Use Minecraft.getInstance().gameRenderer.getMainCamera().rotation() instead.
public class BubbleRenderer extends EntityRenderer<BubbleEntity, EntityRenderState> {
    public static Identifier TEXTURE = ArsNouveau.prefix("textures/entity/bubble.png");
    public static Identifier POP_1 = ArsNouveau.prefix("textures/entity/bubble_pop1.png");
    public static Identifier POP_2 = ArsNouveau.prefix("textures/entity/bubble_pop2.png");
    public static Identifier POP_3 = ArsNouveau.prefix("textures/entity/bubble_pop3.png");
    public static Identifier POP_4 = ArsNouveau.prefix("textures/entity/bubble_pop4.png");
    public static Identifier POP_5 = ArsNouveau.prefix("textures/entity/bubble_pop5.png");

    public BubbleRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager);
    }

    @Override
    public EntityRenderState createRenderState() {
        return new EntityRenderState();
    }

    // 1.21.11: EntityRenderer no longer has getTextureLocation(T entity) — removed @Override
    public Identifier getTextureLocation(BubbleEntity pEntity) {
        return TEXTURE;
    }

    @Override
    public void submit(EntityRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
        super.submit(state, poseStack, collector, cameraState);
        // TODO: Port BubbleEntity rendering - entity-specific data (passengers, poppingTicks) must be
        // stored in a custom render state via extractRenderState. For now this is a stub.
    }

    /**
     * Static helper used by RenderFlyingItem. Entity may be null when called from render state context.
     * If entity is null, renders a default bubble texture without pop animation.
     */
    public static void renderBubble(Entity entityIn, net.minecraft.client.renderer.entity.EntityRenderDispatcher entityRenderDispatcher, PoseStack matrixStack, MultiBufferSource buffer) {
        double y = (entityIn != null && !entityIn.getPassengers().isEmpty()) ? entityIn.getBbHeight() : 0.25f;
        matrixStack.pushPose();
        matrixStack.translate(0, y, 0);
        // MC 1.21.11: cameraOrientation() removed. Use camera rotation quaternion.
        matrixStack.mulPose(Minecraft.getInstance().gameRenderer.getMainCamera().rotation());

        matrixStack.scale(0.025F, -0.025F, 0.025F);
        float base = 2.0f;
        if (entityIn != null) {
            var passenger = entityIn.getFirstPassenger();
            if (passenger != null) {
                base += 1f;
                base *= passenger.getBbWidth() / entityIn.getBbWidth();
            }
        }
        matrixStack.scale(base, base, base);
        final Matrix4f pose = matrixStack.last().pose();
        Identifier texture = TEXTURE;

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
