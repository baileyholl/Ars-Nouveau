package com.hollingsworth.arsnouveau.client.renderer.item;

import com.hollingsworth.arsnouveau.common.items.AnimBlockItem;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class GenericItemBlockRenderer extends GeoItemRenderer<AnimBlockItem> {

    public boolean isTranslucent;

    public GenericItemBlockRenderer(GeoModel modelProvider) {
        super(new GenericItemModel(modelProvider));
    }

    public GenericItemBlockRenderer withTranslucency() {
        this.isTranslucent = true;
        return this;
    }

    @Override
    public RenderType getRenderType(AnimBlockItem animatable, ResourceLocation texture, @org.jetbrains.annotations.Nullable MultiBufferSource bufferSource, float partialTick) {
        return this.isTranslucent ? RenderType.entityTranslucent(texture) : super.getRenderType(animatable, texture, bufferSource, partialTick);
    }
}
