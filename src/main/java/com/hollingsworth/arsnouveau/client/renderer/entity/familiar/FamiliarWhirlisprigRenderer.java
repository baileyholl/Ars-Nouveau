package com.hollingsworth.arsnouveau.client.renderer.entity.familiar;

import com.hollingsworth.arsnouveau.client.renderer.entity.WhirlisprigModel;
import com.hollingsworth.arsnouveau.common.entity.familiar.FamiliarWhirlisprig;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

// GeckoLib 5: old render(T, float, float, PoseStack, MultiBufferSource, int) removed - use submit pipeline
// GeoBone.getBone() removed - bone access requires new GeckoLib 5 API
// TODO: Port particle emission (sylph/propellers bone offsets) to GeckoLib 5 addPerBoneRender pattern
public class FamiliarWhirlisprigRenderer extends GenericFamiliarRenderer<FamiliarWhirlisprig> {

    public FamiliarWhirlisprigRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new WhirlisprigModel<>());
    }
}
