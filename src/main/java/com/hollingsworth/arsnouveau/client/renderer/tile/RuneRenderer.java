package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.registry.ParticleTimelineRegistry;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.client.renderer.item.GenericItemBlockRenderer;
import com.hollingsworth.arsnouveau.common.block.BasicSpellTurret;
import com.hollingsworth.arsnouveau.common.block.tile.RuneTile;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.RenderPassInfo;

public class RuneRenderer extends ArsGeoBlockRenderer<RuneTile> {

    private static final DataTicket<Identifier> RUNE_TEXTURE = DataTicket.create("ars_nouveau:rune_texture", Identifier.class);
    private static final DataTicket<Direction> RUNE_DIRECTION = DataTicket.create("ars_nouveau:rune_direction", Direction.class);

    public static GenericModel<RuneTile> model = new RuneModel();

    public static class RuneModel extends GenericModel<RuneTile> {
        public RuneModel() {
            super("rune", "block/runes");
        }

        @Override
        public Identifier getTextureResource(GeoRenderState renderState) {
            Identifier tex = renderState.getOrDefaultGeckolibData(RUNE_TEXTURE, (Identifier) null);
            return tex != null ? tex : textLoc;
        }
    }


    public RuneRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
        super(rendererDispatcherIn, model);
    }

    @Override
    public void captureDefaultRenderState(RuneTile animatable, Void relatedObject, ArsBlockEntityRenderState renderState, float partialTick) {
        super.captureDefaultRenderState(animatable, relatedObject, renderState, partialTick);
        if (animatable != null) {
            Spell spell = animatable.spell;
            if (spell != null) {
                String pattern = spell.particleTimeline().get(ParticleTimelineRegistry.RUNE_TIMELINE.get()).getTexture();
                renderState.addGeckolibData(RUNE_TEXTURE, ArsNouveau.prefix("textures/block/runes/" + pattern + ".png"));
            }
            renderState.addGeckolibData(RUNE_DIRECTION, animatable.getBlockState().getValue(BasicSpellTurret.FACING));
        }
    }

    @Override
    public void adjustRenderPose(RenderPassInfo<ArsBlockEntityRenderState> renderPassInfo) {
        // GeckoLib 5.4.5: preRenderPass already translates (0.5, 0, 0.5) to center the model.
        // DO NOT call super (which would add tryRotateByBlockstate, conflicting with our rotations).
        // All translations here are relative to the post-preRenderPass origin (block center XZ).
        Direction direction = renderPassInfo.renderState().getOrDefaultGeckolibData(RUNE_DIRECTION, (Direction) null);
        if (direction == null) return;
        var poseStack = renderPassInfo.poseStack();
        switch (direction) {
            case UP -> {
                // Model is already a flat XZ quad facing up; preRenderPass centering is sufficient.
            }
            case DOWN -> {
                poseStack.translate(0, 0.98, 0);
                poseStack.mulPose(Axis.XP.rotationDegrees(90));
            }
            case NORTH -> {
                poseStack.translate(0, 0.5, 0.5);
                poseStack.mulPose(Axis.XP.rotationDegrees(90));
                poseStack.mulPose(Axis.ZP.rotationDegrees(180));
                poseStack.mulPose(Axis.YP.rotationDegrees(180));
            }
            case EAST -> {
                poseStack.translate(-0.5, 0.5, 0);
                poseStack.mulPose(Axis.ZP.rotationDegrees(-90));
                poseStack.mulPose(Axis.YP.rotationDegrees(180));
            }
            case WEST -> {
                poseStack.translate(0.5, 0.5, 0);
                poseStack.mulPose(Axis.ZP.rotationDegrees(90));
            }
            case SOUTH -> {
                poseStack.translate(0, 0.5, -0.5);
                poseStack.mulPose(Axis.XP.rotationDegrees(-90));
                poseStack.mulPose(Axis.ZP.rotationDegrees(-180));
                poseStack.mulPose(Axis.YP.rotationDegrees(180));
            }
        }
    }

    @Override
    public RenderType getRenderType(ArsBlockEntityRenderState renderState, Identifier texture) {
        return RenderTypes.entityCutoutNoCull(texture);
    }

    @Override
    public int getRenderColor(RuneTile animatable, Void renderState, float partialTick) {
        var color = animatable.getColor();
        return animatable.isCharged ? (0xFF000000 | color.getColor()) : super.getRenderColor(animatable, renderState, partialTick);
    }

    public static GenericItemBlockRenderer getISTER() {
        return new GenericItemBlockRenderer(model);
    }
}
