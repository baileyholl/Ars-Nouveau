package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.common.entity.EnchantedSkull;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

// Skull block entity rendered as falling block — inherits EnchantedFallingBlockRenderer.
// renderSkull() removed: skull block state is rendered via submitMovingBlock in parent.
public class EnchantedSkullRenderer extends EnchantedFallingBlockRenderer<EnchantedSkull> {
    public EnchantedSkullRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
    }
}
