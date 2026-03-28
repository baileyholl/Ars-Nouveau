package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.common.block.tile.EnchantingApparatusTile;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class EnchantingApparatusRenderer extends ArsGeoBlockRenderer<EnchantingApparatusTile> {

    public EnchantingApparatusRenderer(BlockEntityRendererProvider.Context p_i226006_1_) {
        super(p_i226006_1_, new GenericModel<>("enchanting_apparatus"));
    }
    // TODO: GeckoLib 5 - item rendering (renderFinal) and block rotation (rotateBlock) removed.
    // Re-implement item display via GeckoLib 5 hooks when API is available.
}
