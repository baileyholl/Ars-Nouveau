package com.hollingsworth.arsnouveau.client.renderer.entity.familiar;

import com.hollingsworth.arsnouveau.client.renderer.entity.AnimBlockRenderer;
import com.hollingsworth.arsnouveau.common.entity.AnimHeadSummon;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

// GeckoLib 5.4.2 migration:
// - renderRecursively() REMOVED; skull rendering at "block" bone needs to be ported to preRenderPass/addPerBoneRender
// TODO: Port skull rendering at bone "block" to GeckoLib 5 addPerBoneRender:
//   - Apply YP 180 rotation + translate(0, 0.2, 0) + scale(1.4, 1.4, 1.4)
//   - Call EnchantedSkullRenderer.renderSkull(...) using stored stack from render state
public class AnimSkullRenderer extends AnimBlockRenderer<AnimHeadSummon> {
    public AnimSkullRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager);
    }
}
