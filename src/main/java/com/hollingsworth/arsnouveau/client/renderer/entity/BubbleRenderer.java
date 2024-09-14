package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.registry.ShaderRegistry;
import com.hollingsworth.arsnouveau.client.renderer.tile.GenericModel;
import com.hollingsworth.arsnouveau.common.entity.BubbleEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BubbleRenderer extends GeoEntityRenderer<BubbleEntity> {

    public BubbleRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new GenericModel<>("bubble", "entity").withEmptyAnim());
    }

    @Override
    public void actuallyRender(PoseStack matrixStack, BubbleEntity entityIn, BakedGeoModel model, @Nullable RenderType renderType, MultiBufferSource buffer, @Nullable VertexConsumer ver, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        double y = entityIn.getBbHeight();
        matrixStack.pushPose();
        matrixStack.translate(0, y, 0);
        matrixStack.mulPose(entityRenderDispatcher.cameraOrientation());

        matrixStack.scale(0.025F, -0.025F, 0.025F);
        float base = 3.0f;
        var passenger = entityIn.getFirstPassenger();
        if(passenger != null){
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
