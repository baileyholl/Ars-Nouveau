package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.client.renderer.item.GenericItemBlockRenderer;
import com.hollingsworth.arsnouveau.common.block.tile.ArcaneCoreTile;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import software.bernie.geckolib.model.GeoModel;

public class ArcaneCoreRenderer extends ArsGeoBlockRenderer<ArcaneCoreTile> {
    public static GeoModel model = new GenericModel<>("arcane_core");

    public ArcaneCoreRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
        this(rendererDispatcherIn, model);
    }

    public ArcaneCoreRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn, GeoModel<ArcaneCoreTile> modelProvider) {
        super(rendererDispatcherIn, modelProvider);
    }

    public static GenericItemBlockRenderer getISTER() {
        return new GenericItemBlockRenderer(model);
    }

    @Override
    protected void rotateBlock(Direction facing, PoseStack poseStack) {
        if (facing.getAxis().isHorizontal()) {
            var step = facing.step();
            poseStack.translate(-step.x * 0.5, 0.5, -step.z * 0.5);
        } else if (facing == Direction.DOWN) {
            poseStack.translate(0, 1.0, 0);
        }
        poseStack.mulPose(facing.getRotation());
    }
}
