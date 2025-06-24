package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.registry.ParticleTimelineRegistry;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.client.renderer.item.GenericItemBlockRenderer;
import com.hollingsworth.arsnouveau.common.block.BasicSpellTurret;
import com.hollingsworth.arsnouveau.common.block.tile.RuneTile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.util.Color;

public class RuneRenderer extends ArsGeoBlockRenderer<RuneTile> {

    public static GenericModel<RuneTile> model = new RuneModel();

    public static class RuneModel extends GenericModel<RuneTile> {
        public RuneModel() {
            super("rune", "block/runes");
        }

        @Override
        public ResourceLocation getTextureResource(RuneTile rune) {
            if (rune == null) return super.getTextureResource(rune);
            Spell spell = rune.spell;
            if (spell == null) {
                return super.getTextureResource(rune);
            }
            String pattern = spell.particleTimeline().get(ParticleTimelineRegistry.RUNE_TIMELINE.get()).getTexture();
            return ArsNouveau.prefix("textures/block/runes/" + pattern + ".png");
        }
    }


    public RuneRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
        super(rendererDispatcherIn, model);
    }

    @Override
    public void preRender(PoseStack poseStack, RuneTile animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int color) {

    }

    @Override
    public void renderFinal(PoseStack poseStack, RuneTile animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay, int color) {

    }

    @Override
    public void actuallyRender(PoseStack poseStack, RuneTile animatable, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int color) {
        poseStack.pushPose();
        Direction direction = animatable.getBlockState().getValue(BasicSpellTurret.FACING);
//        poseStack.translate(0.5, 0.5, 0);
        switch (direction) {
            case UP -> {
                poseStack.translate(0.5, 0, 0.5);
                poseStack.mulPose(Axis.XP.rotationDegrees(-90));
            }
            case DOWN -> {
                poseStack.translate(0.5, 0.98, 0.5);
                poseStack.mulPose(Axis.XP.rotationDegrees(90));
            }
            case NORTH -> {
                poseStack.translate(0.5, 0.5, 1);
                poseStack.mulPose(Axis.XP.rotationDegrees(90));
                poseStack.mulPose(Axis.ZP.rotationDegrees(180));
                poseStack.mulPose(Axis.YP.rotationDegrees(180));
            }
            case EAST -> {
                poseStack.translate(0, 0.5, 0.5);
                poseStack.mulPose(Axis.ZP.rotationDegrees(-90));
                poseStack.mulPose(Axis.YP.rotationDegrees(180));
            }
            case WEST -> {
                poseStack.translate(1, 0.5, 0.5);
                poseStack.mulPose(Axis.ZP.rotationDegrees(90));
            }
            case SOUTH -> {
                poseStack.translate(0.5, 0.5, 0);
                poseStack.mulPose(Axis.XP.rotationDegrees(-90));
                poseStack.mulPose(Axis.ZP.rotationDegrees(-180));
                poseStack.mulPose(Axis.YP.rotationDegrees(180));
            }
        }
        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, color);
        poseStack.popPose();
    }

    @Override
    public RenderType getRenderType(RuneTile animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityCutoutNoCull(texture);
    }

    @Override
    public Color getRenderColor(RuneTile animatable, float partialTick, int packedLight) {
        var color = animatable.getColor();
        return animatable.isCharged ? Color.ofOpaque(color.getColor()) : super.getRenderColor(animatable, partialTick, packedLight);
    }

    public static GenericItemBlockRenderer getISTER() {
        return new GenericItemBlockRenderer(model);
    }
}