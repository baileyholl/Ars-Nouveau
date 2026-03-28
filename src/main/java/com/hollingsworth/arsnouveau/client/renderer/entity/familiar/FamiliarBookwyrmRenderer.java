package com.hollingsworth.arsnouveau.client.renderer.entity.familiar;

import com.hollingsworth.arsnouveau.client.renderer.entity.BookwyrmModel;
import com.hollingsworth.arsnouveau.common.entity.familiar.FamiliarBookwyrm;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

// GeckoLib 5: withScale(0.5f) replaces the old 0.5x PoseStack scale in render()
public class FamiliarBookwyrmRenderer extends GenericFamiliarRenderer<FamiliarBookwyrm> {
    public FamiliarBookwyrmRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new BookwyrmModel<>());
        withScale(0.5f);
    }
}
