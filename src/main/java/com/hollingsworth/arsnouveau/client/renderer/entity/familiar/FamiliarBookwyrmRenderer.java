package com.hollingsworth.arsnouveau.client.renderer.entity.familiar;

import com.hollingsworth.arsnouveau.client.renderer.entity.BookwyrmModel;
import com.hollingsworth.arsnouveau.common.entity.familiar.FamiliarBookwyrm;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

// TODO: 1.21.11 - render(T, float, float, PoseStack, MultiBufferSource, int) removed.
// Port 0.5x scale to 1.21.11 renderer pattern (extractRenderState + submit pipeline).
public class FamiliarBookwyrmRenderer extends GenericFamiliarRenderer<FamiliarBookwyrm> {
    public FamiliarBookwyrmRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new BookwyrmModel<>());
    }
}
