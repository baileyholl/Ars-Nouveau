package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.client.renderer.item.GenericItemBlockRenderer;
import com.hollingsworth.arsnouveau.common.block.tile.RepositoryTile;
import com.hollingsworth.arsnouveau.common.items.AnimBlockItem;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.util.Color;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib3.util.EModelRenderCycle;

public class RepositoryRenderer extends ArsGeoBlockRenderer<RepositoryTile> {
    public static GeoModel<RepositoryTile> model = new RepositoryModel();
    public RepositoryRenderer(BlockEntityRendererProvider.Context rendererProvider) {
        super(rendererProvider, model);
    }

    @Override
    public void render(RepositoryTile tile, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(tile, partialTick, poseStack, bufferSource, packedLight);

    }

    @Override
    public void render(GeoModel model, RepositoryTile repo, float partialTick, RenderType type, PoseStack poseStack, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        super.render(model, repo, partialTick, type, poseStack, bufferSource, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    public static GenericItemBlockRenderer getISTER() {
            return new GenericItemBlockRenderer(model) {
                @Override
                public void render(AnimBlockItem animatable, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, ItemStack stack) {
                    this.currentItemStack = stack;
                    GeoModel model = this.modelProvider.getModel(this.modelProvider.getModelResource(animatable));
                    this.dispatchedMat = poseStack.last().pose().copy();

                    setCurrentModelRenderCycle(EModelRenderCycle.INITIAL);
                    int fillLevel = 0;

                    hideBones(model, fillLevel);

                    poseStack.pushPose();
                    poseStack.translate(0.5f, 0.51f, 0.5f);

                    RenderSystem.setShaderTexture(0, getTextureLocation(animatable));
                    Color renderColor = getRenderColor(animatable, 0, poseStack, bufferSource, null, packedLight);
                    RenderType renderType = getRenderType(animatable, 0, poseStack, bufferSource, null, packedLight,
                            getTextureLocation(animatable));
                    render(model, animatable, 0, renderType, poseStack, bufferSource, null, packedLight, OverlayTexture.NO_OVERLAY,
                            renderColor.getRed() / 255f, renderColor.getGreen() / 255f,
                            renderColor.getBlue() / 255f, renderColor.getAlpha() / 255f);
                    poseStack.popPose();
                }

                @SuppressWarnings("OptionalGetWithoutIsPresent")
                void hideBones(GeoModel model, int level) {
                    try {
                        model.getBone("1").get().setHidden(level == 0);
                        model.getBone("2_3").get().setHidden(level < 3);
                        model.getBone("4_6").get().setHidden(level < 5);
                        model.getBone("7_9").get().setHidden(level < 7);
                        model.getBone("10_12").get().setHidden(level < 9);
                        model.getBone("13_15").get().setHidden(level < 11);
                        model.getBone("16_18").get().setHidden(level < 12);
                        model.getBone("19_21").get().setHidden(level < 13);
                        model.getBone("22_24").get().setHidden(level < 14);
                        model.getBone("25_27").get().setHidden(level < 15);
                    } catch (Exception ignored) {
                    }
                }
            };
    }
}
