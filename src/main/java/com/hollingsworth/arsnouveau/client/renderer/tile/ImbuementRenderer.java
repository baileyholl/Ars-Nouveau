package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.common.block.tile.ImbuementTile;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class ImbuementRenderer extends ArsGeoBlockRenderer<ImbuementTile> {

    public ImbuementRenderer(BlockEntityRendererProvider.Context p_i226006_1_) {
        super(p_i226006_1_, new GenericModel<>("imbuement_chamber"));
    }

    // TODO: Port item rendering to preRenderPass/addRenderLayers when available in GeckoLib 5.
    // actuallyRender() was removed in GeckoLib 5; rotateBlock() was also removed.
    // ItemRenderer.renderStatic() was removed in MC 1.21.11; use ItemStackRenderState API.

}
