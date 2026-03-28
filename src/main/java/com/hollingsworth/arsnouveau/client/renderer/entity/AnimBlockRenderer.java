package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.block.MageBlock;
import com.hollingsworth.arsnouveau.common.entity.AnimBlockSummon;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.RenderTypeHelper;
import net.neoforged.neoforge.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.RenderPassInfo;


// GeckoLib 5.4.2 migration:
// - GeoEntityRenderer now requires 2 type params <T, R extends EntityRenderState & GeoRenderState>
// - renderRecursively() and preRender() REMOVED
// - getRenderColor signature changed: now returns int (was Color)
// - Block rendering API changed: BlockStateModel.collectParts() + ModelBlockRenderer.tesselateBlock(List<BlockModelPart>)
// - ModelData moved to net.neoforged.neoforge.model.data.ModelData
// - software.bernie.geckolib.util.Color REMOVED; use int (ARGB packed)
// TODO: Port bone-specific block rendering to preRenderPass / addPerBoneRender
public class AnimBlockRenderer<BOBBY extends AnimBlockSummon> extends GeoEntityRenderer<BOBBY, ArsEntityRenderState> {
    public static MultiBufferSource.BufferSource buffer = MultiBufferSource.immediate(new ByteBufferBuilder(1536));
    protected static final Identifier TEXTURE = ArsNouveau.prefix("textures/entity/anim_block.png");
    public static final Identifier BASE_MODEL = ArsNouveau.prefix("animated_block");
    public static final Identifier ANIMATIONS = ArsNouveau.prefix("animated_block_animations");

    protected final BlockRenderDispatcher dispatcher;
    // bufferSource stored during preRenderPass for block rendering
    protected MultiBufferSource bufferSource;

    public AnimBlockRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new GeoModel<BOBBY>() {
            @Override
            public Identifier getModelResource(software.bernie.geckolib.renderer.base.GeoRenderState renderState) {
                return BASE_MODEL;
            }

            @Override
            public Identifier getTextureResource(software.bernie.geckolib.renderer.base.GeoRenderState renderState) {
                return TEXTURE;
            }

            @Override
            public Identifier getAnimationResource(BOBBY animatable) {
                return ANIMATIONS;
            }

            // GeckoLib 5: setCustomAnimations removed; bone hiding needs captureDefaultRenderState/addPerBoneRender
            // TODO: Port "block" bone hiding based on MageBlock check to GeckoLib 5 pattern
        });
        dispatcher = renderManager.getBlockRenderDispatcher();
    }

    @Override
    public ArsEntityRenderState createRenderState(@NotNull BOBBY entity, Void unused) {
        return new ArsEntityRenderState();
    }

    @Override
    public void scaleModelForRender(RenderPassInfo<ArsEntityRenderState> renderPassInfo, float width, float height) {
        renderPassInfo.poseStack().scale(0.8F, 0.8F, 0.8F);
        super.scaleModelForRender(renderPassInfo, width, height);
    }

    // GeckoLib 5: getRenderColor returns int (ARGB packed), not geckolib Color
    @Override
    public int getRenderColor(BOBBY animatable, java.lang.Void context, float partialTick) {
        if (animatable != null) {
            ParticleColor color = ParticleColor.fromInt(animatable.getColor());
            // Pack as ARGB with 75% alpha
            int alpha = (int)(0.75f * 255);
            int r = (int)(color.getRed() * 255);
            int g = (int)(color.getGreen() * 255);
            int b = (int)(color.getBlue() * 255);
            return (alpha << 24) | (r << 16) | (g << 8) | b;
        }
        return super.getRenderColor(animatable, context, partialTick);
    }

    // TODO: Port "block" bone rendering to preRenderPass/addPerBoneRender in GeckoLib 5.
    // Previously in renderRecursively, when bone == "block", the actual block model was rendered at the bone's pivot.
    // This needs captureDefaultRenderState to store BlockState, then in preRenderPass use addPerBoneRender("block", ...).
    // Block rendering now uses: dispatcher.getBlockModel(blockState).collectParts(random, parts)
    // Then: dispatcher.getModelRenderer().tesselateBlock(level, parts, blockState, blockPos, poseStack, bufferSource::getBuffer, false, packedOverlay)
}
