package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.common.block.tile.ScryerPlanariumTile;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

// GeckoLib 5.4.2: GeoBlockRenderer requires R extends BlockEntityRenderState & GeoRenderState.
// ArsBlockEntityRenderState satisfies this at compile time.
public class ScryerPlanariumRenderer extends GeoBlockRenderer<ScryerPlanariumTile, ArsBlockEntityRenderState> {

    public ScryerPlanariumRenderer(BlockEntityRendererProvider.Context blockRenderDispatcher) {
        super(new ScryerPlanariumModel());
    }

    @Override
    public ArsBlockEntityRenderState createRenderState() {
        return new ArsBlockEntityRenderState();
    }
}
