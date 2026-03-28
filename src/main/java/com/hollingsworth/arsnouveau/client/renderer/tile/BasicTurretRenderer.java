package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.client.renderer.item.GenericItemBlockRenderer;
import com.hollingsworth.arsnouveau.common.block.BasicSpellTurret;
import com.hollingsworth.arsnouveau.common.block.tile.BasicSpellTurretTile;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.RenderPassInfo;

// TODO: Bone rotation (setRotX/setRotY) for RotatingTurretTile needs migration to addAdditionalStateData in GeckoLib 5.
// The setCustomAnimations override was removed because the method no longer exists in GeoModel.
public class BasicTurretRenderer extends ArsGeoBlockRenderer<BasicSpellTurretTile> {
    public static GeoModel model = new GenericModel("basic_spell_turret");

    public BasicTurretRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
        this(rendererDispatcherIn, model);
    }

    public BasicTurretRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn, GeoModel<BasicSpellTurretTile> modelProvider) {
        super(rendererDispatcherIn, modelProvider);
    }

    // GeckoLib 5: actuallyRender replaced with adjustRenderPose for pose manipulation
    @Override
    public void adjustRenderPose(RenderPassInfo<ArsBlockEntityRenderState> renderPassInfo) {
        super.adjustRenderPose(renderPassInfo);
        PoseStack poseStack = renderPassInfo.poseStack();
        Direction direction = renderPassInfo.renderState().blockState.getValue(BasicSpellTurret.FACING);
        if (direction == Direction.UP) {
            poseStack.translate(0, 0.5, -0.5);
        } else if (direction == Direction.DOWN) {
            poseStack.translate(0, 0.5, 0.5);
        }
    }

    public static GenericItemBlockRenderer getISTER() {
        return new GenericItemBlockRenderer(model);
    }
}
