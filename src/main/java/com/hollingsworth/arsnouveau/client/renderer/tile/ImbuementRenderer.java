package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.common.block.tile.ImbuementTile;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.renderer.base.RenderPassInfo;

public class ImbuementRenderer extends ArsGeoBlockRenderer<ImbuementTile> {

    public ImbuementRenderer(BlockEntityRendererProvider.Context p_i226006_1_) {
        super(p_i226006_1_, new GenericModel<>("imbuement_chamber"));
    }

    // ImbuementBlock uses 6-way FACING (UP/DOWN/NSEW via getClickedFace) — same as apparatus.
    // GeckoLib 5 tryRotateByBlockstate misrotates UP-placed blocks, flattening the model.
    // Override to suppress it; imbuement chamber is symmetric and UP is the natural placement.
    @Override
    public void adjustRenderPose(RenderPassInfo<ArsBlockEntityRenderState> renderPassInfo) {}

    // TODO: Port item rendering to preRenderPass/addRenderLayers when available in GeckoLib 5.
    // actuallyRender() was removed in GeckoLib 5; rotateBlock() was also removed.
    // ItemRenderer.renderStatic() was removed in MC 1.21.11; use ItemStackRenderState API.

}
