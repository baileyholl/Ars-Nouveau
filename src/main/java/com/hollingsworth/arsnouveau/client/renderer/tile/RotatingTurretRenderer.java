package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.common.block.tile.RotatingTurretTile;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.renderer.base.RenderPassInfo;

// GeckoLib 5 migration: actuallyRender and rotateBlock removed.
// TODO: Move rotation interpolation to a tick event or store it in render state via captureDefaultRenderState.
public class RotatingTurretRenderer extends ArsGeoBlockRenderer<RotatingTurretTile> {

    public RotatingTurretRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
        super(rendererDispatcherIn, BasicTurretRenderer.model);
    }

    // GeckoLib 5: tryRotateByBlockstate is called internally. Override adjustRenderPose to disable auto-rotation.
    @Override
    public void adjustRenderPose(RenderPassInfo<ArsBlockEntityRenderState> renderPassInfo) {
        // Intentionally skip super to disable automatic blockstate-based rotation from GeoBlockRenderer
    }
}
