package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.common.entity.EnchantedFallingBlock;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

// 1.21.11: old render(T, float, float, PoseStack, MultiBufferSource, int) removed
// TODO: Port 0.5f scale to a different mechanism if needed for CinderRenderer
public class CinderRenderer<T extends EnchantedFallingBlock> extends EnchantedFallingBlockRenderer<T> {
    public CinderRenderer(EntityRendererProvider.Context p_174112_) {
        super(p_174112_);
    }
}
